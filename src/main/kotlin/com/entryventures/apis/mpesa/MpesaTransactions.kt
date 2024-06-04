package com.entryventures.apis.mpesa

import com.entryventures.apis.ApiCallWrapper
import com.entryventures.apis.Apis
import com.entryventures.apis.mpesa.b2c.B2CRequestPayload
import com.entryventures.apis.mpesa.b2c.SyncB2CRequestAcceptanceResponse
import com.entryventures.apis.mpesa.express.StkRequestPayload
import com.entryventures.apis.mpesa.express.SyncMpesaExpressResponse
import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.exceptions.ThirdPartyApiException
import com.entryventures.security.PasswordService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class MpesaTransactions(
    private val apis: Apis
): ApiCallWrapper {

    @Value("\${apis.mpesa.basic-auth-token}")
    private lateinit var mpesaBasicAuthorizationToken: String

    @Value("\${apis.mpesa.business-shortcode}")
    private lateinit var mpesaBusinessShortCode: String

    @Value("\${apis.mpesa.merchant-passkey}")
    private lateinit var merchantPasskey: String

    @Value("\${apis.mpesa.initiator-name}")
    private lateinit var initiatorName: String

    @Value("\${apis.mpesa.queue-timeout-url}")
    private lateinit var queueTimeOutUrl: String

    @Value("\${apis.mpesa.b2c.result-url}")
    private lateinit var b2cResultUrl: String

    override suspend fun <P, E> call(apiCall: suspend () -> P): P? {
        return apis.httpRequestWrapper<P, E> (
            request = { apiCall() },
            clientError = { statusCode, resBody ->
                throw EntryVenturesException(
                    serverStatus = statusCode,
                    errorMessage = { "Client Error" },
                    errorDescription = {
                        mapOf(
                            "status" to statusCode,
                            "message" to "Safaricom encountered an error processing request. Please check request properties and try again",
                            "error" to resBody
                        )
                    }
                )
            },
            serverError = { statusCode, resBody ->
                throw ThirdPartyApiException(
                    status = statusCode,
                    party = "SAFARICOM",
                    errorMessage = "Internal server Error",
                    exceptionDescription = {
                        mapOf(
                            "status" to statusCode,
                            "message" to "Safaricom encountered an internal server error. Please try again later",
                            "error" to resBody
                        )
                    }
                )
            },
            connectionError = {
                throw ThirdPartyApiException(
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                    party = "SAFARICOM",
                    errorMessage = "Connection Error",
                    exceptionDescription = {
                        "Could not establish connection to safaricom."
                    }
                )
            },
            unknownHostError = {
                throw ThirdPartyApiException(
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                    party = "SAFARICOM",
                    errorMessage = "Unknown Host Exception",
                    exceptionDescription = {
                        "Could not resolve host"
                    }
                )
            }
        )
    }

    suspend fun requestMpesaAccessToken(): MpesaAccessTokenResponse? {
        return call<MpesaAccessTokenResponse, Map<String, Any>?> {
            apis.mpesaClient.accessToken(
                authorization = "Basic $mpesaBasicAuthorizationToken"
            )
        }
    }


    suspend fun mpesaExpress(
        amount: Long,
        phone: Long,
        accountReference: String,
        transactionDescription: String
    ): SyncMpesaExpressResponse? {

        val timeStamp = PasswordService.timeStamp()
        val payload = StkRequestPayload(
            businessShortCode = mpesaBusinessShortCode,
            password = PasswordService.toBase64(
                mpesaBusinessShortCode,
                merchantPasskey,
                timeStamp
            ),
            timestamp = timeStamp,
            transactionType = "CustomerPayBillOnline",
            amount = "$amount",
            partyA = "$phone",
            partyB = mpesaBusinessShortCode,
            phoneNumber = "$phone",
            callBackURL = "https://grid-online.vercel.app", // "http://localhost:8080/entry-ventures/mpesa/callback/stk",
            accountReference = accountReference,
            transactionDesc = transactionDescription
        )

        return call<SyncMpesaExpressResponse?, Map<String, String>?> {
            requestMpesaAccessToken()?.let { accessToken ->
                apis.mpesaClient.mpesaExpress(
                    authorization = "Bearer ${accessToken.accessToken}",
                    payload = payload
                )
            }
        }
    }

    /**
     * Busness to Customer Transaction
     * @param transactionId A unique transaction identifier
     * @param remarks Brief description of the transaction
     * @param occasion Intended occasion for the transaction
     * @param customer A registered MPESA user
     * @param amount Amount to be transfered from the organization identified by the business shortcode to the customer
     * @return Returns a status of the transaction
     */
    suspend fun b2c(
        transactionId: String,
        remarks: String,
        occasion: String,
        customer: RegisteredMpesaClient,
        amount: Float
    ): SyncB2CRequestAcceptanceResponse? {
        // Obtain mpesa authorization access token
        return requestMpesaAccessToken()?.let { mpesaAccessTokenResponse ->
            // Successful access token request
            call<SyncB2CRequestAcceptanceResponse, Map<String, String>?>{
                apis.mpesaClient.b2c(
                    payload = B2CRequestPayload(
                        originatorConversationID = transactionId,
                        initiatorName = initiatorName,
                        commandID = "BusinessPayment",
                        amount = "${amount.roundToInt()}",
                        partyA = mpesaBusinessShortCode,
                        partyB = "254${customer.phone}",
                        remarks = remarks,
                        queueTimeOutURL = queueTimeOutUrl,
                        resultURL = b2cResultUrl,
                        occasion = occasion
                    ),
                    authorization = "Bearer ${mpesaAccessTokenResponse.accessToken}"
                )
            }
        }
    }
}