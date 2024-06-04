package com.entryventures.apis.equity

import com.entryventures.apis.ApiCallWrapper
import com.entryventures.apis.Apis
import com.entryventures.apis.equity.dto.*
import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.exceptions.ThirdPartyApiException
import com.entryventures.extensions.toBase64
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class JengaTransactions(
    private val apis: Apis,
    private val jengaKeyService: JengaKeyService
): ApiCallWrapper {

    @Value("\${apis.jenga.api-key}")
    lateinit var jengaApiKey: String

    @Value("\${apis.jenga.merchant-code}")
    lateinit var jengaMerchantCode: String

    @Value("\${apis.jenga.consumer-secret}")
    lateinit var jengaConsumerKey: String

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
                            "message" to "JENGA encountered an error processing request. Please check request properties and try again",
                            "error" to resBody
                        )
                    }
                )
            },
            serverError = { statusCode, resBody ->
                throw ThirdPartyApiException(
                    status = statusCode,
                    party = "JENGA",
                    errorMessage = "Internal server Error",
                    exceptionDescription = {
                        mapOf(
                            "status" to statusCode,
                            "message" to "JENGA encountered an internal server error. Please try again later",
                            "error" to resBody
                        )
                    }
                )
            },
            connectionError = {
                throw ThirdPartyApiException(
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                    party = "JENGA",
                    errorMessage = "Connection Error",
                    exceptionDescription = {
                        "Could not establish connection to JENGA."
                    }
                )
            },
            unknownHostError = {
                throw ThirdPartyApiException(
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                    party = "JENGA",
                    errorMessage = "Unknown Host Exception",
                    exceptionDescription = {
                        "Could not resolve host"
                    }
                )
            }
        )
    }

    // Autheticate with Jenga for an Authorization token
    suspend fun requestJengaAccessToken(): JengaAccessTokenResponse? {
        return call<JengaAccessTokenResponse, JengaResponse>(
            apiCall = {
                apis.equityJengaClient.accessToken(
                    apiKey = jengaApiKey,
                    payload = JengaAccessTokenRequestPayload(
                        merchantCode = jengaMerchantCode,
                        consumerSecret = jengaConsumerKey
                    ),
                    contentType = "application/json"
                )
            }
        )
    }

    // Obtain the base account's balance
    suspend fun queryAccountBalance(
        countryCode: String,
        accountId: String
    ): JengaResponse? {

        return call<JengaResponse?, JengaResponse>(
            apiCall = {
                requestJengaAccessToken()?.let { jengaAccessTokenResponse ->
                    apis.equityJengaClient.queryAccountBalance(
                        authorization = "Bearer ${jengaAccessTokenResponse.accessToken}",
                        signature = jengaKeyService
                            .generateSignature(
                                jengaKeyService.signaturePayload(countryCode, accountId)
                            ).toBase64(),
                        countryCode = countryCode,
                        accountId = accountId
                    )
                }
            }
        )
    }

    // Perform an account inquiry
    suspend fun accountInquiry(
        countryCode: String,
        accountNumber: String
    ): JengaResponse? {
        return call<JengaResponse?, JengaResponse>(
            apiCall = {
                requestJengaAccessToken()?.let { jengaAccessTokenResponse ->
                    apis.equityJengaClient.accountInquiry(
                        authorization = "Bearer ${jengaAccessTokenResponse.accessToken}",
                        signature = jengaKeyService
                            .generateSignature(
                                jengaKeyService.signaturePayload(countryCode, accountNumber)
                            ).toBase64(),
                        countryCode = countryCode,
                        accountNumber = accountNumber
                    )
                }
            }
        )
    }

    // Obtain Account's full statement
    suspend fun accountFullStatement(
        fullStatementRequestPayload: FullStatementRequestPayload
    ): JengaResponse? {
        return call<JengaResponse?, JengaResponse> (
            apiCall = {
                requestJengaAccessToken()?.let { jengaAccessTokenResponse ->
                    apis.equityJengaClient.accountFullStatement(
                        authorization = "Bearer ${jengaAccessTokenResponse.accessToken}",
                        signature = jengaKeyService
                            .generateSignature(
                                jengaKeyService.signaturePayload(
                                    fullStatementRequestPayload.accountNumber,
                                    fullStatementRequestPayload.countryCode,
                                    fullStatementRequestPayload.toDate
                                )
                            ).toBase64(),
                        fullStatementRequestPayload = fullStatementRequestPayload
                    )
                }
            }
        )
    }

    // Obtain account's mini statement
    suspend fun accountMiniStatement(
        countryCode: String,
        accountNumber: String
    ): JengaResponse? {
        return call<JengaResponse?, JengaResponse>(
            apiCall = {
                requestJengaAccessToken().let { jengaAccessTokenResponse ->
                    apis.equityJengaClient.accountMiniStatement(
                        authorization = "Bearer ${jengaAccessTokenResponse?.accessToken}",
                        signature = jengaKeyService
                            .generateSignature(
                                jengaKeyService.signaturePayload(countryCode, accountNumber)
                            ).toBase64(),
                        countryCode = countryCode,
                        accountNumber = accountNumber,
                    )
                }
            }
        )
    }

    // Check the account's opening and closing balance
    suspend fun accountOpeningAndClosingBalance(
        accountOpeningAndClosingRequestPayload: AccountOpeningAndClosingRequestPayload
    ): JengaResponse? {
        return call<JengaResponse?, JengaResponse>(
            apiCall = {
                requestJengaAccessToken()?.let { jengaAccessTokenResponse ->
                    apis.equityJengaClient.accountOpeningAndClosingBalance(
                        authorization = "Bearer ${jengaAccessTokenResponse.accessToken}",
                        signature = jengaKeyService
                            .generateSignature(
                                jengaKeyService.signaturePayload(
                                    accountOpeningAndClosingRequestPayload.accountId,
                                    accountOpeningAndClosingRequestPayload.countryCode,
                                    accountOpeningAndClosingRequestPayload.date
                                )
                            ).toBase64(),
                        accountOpeningAndClosingRequestPayload = accountOpeningAndClosingRequestPayload
                    )
                }
            }
        )
    }

    // Moving money from Account to mobile wallets
    suspend fun sendToMobileWallet(
        jengaSendToMobileRequestPayload: JengaSendToMobileRequestPayload
    ): JengaResponse? {
        return call<JengaResponse?, JengaResponse>(
            apiCall = {
                requestJengaAccessToken()?.let { jengaAccessTokenResponse ->
                    apis.equityJengaClient.sendToMobileWallet(
                        authorization = "Bearer ${jengaAccessTokenResponse.accessToken}",
                        signature = jengaKeyService
                            .generateSignature(
                                jengaKeyService.signaturePayload(
                                    jengaSendToMobileRequestPayload.transfer.amount,
                                    jengaSendToMobileRequestPayload.transfer.currencyCode,
                                    jengaSendToMobileRequestPayload.transfer.reference,
                                    jengaSendToMobileRequestPayload.source.accountNumber
                                )
                            ).toBase64(),
                        sendToMobilePayload = jengaSendToMobileRequestPayload
                    )
                }
            }
        )
    }

    // Receive money from a mobile wallet
    suspend fun receiveFromMobileWallet(
        jengaReceiveFromMobileRequestPayload: JengaReceiveFromMobileRequestPayload
    ): JengaResponse? {
        return call<JengaResponse?, JengaResponse>(
            apiCall = {
                requestJengaAccessToken()?.let { jengaAccessTokenResponse ->
                    apis.equityJengaClient.receiveFromMobileWallet(
                        authorization = "Bearer ${jengaAccessTokenResponse.accessToken}",
                        signature = jengaKeyService
                            .generateSignature(
                                jengaKeyService.signaturePayload(
                                    ""
                                )
                        ).toBase64(),
                        receiveFromMobilePayload = jengaReceiveFromMobileRequestPayload
                    )
                }
            }
        )
    }

    // Send airtime to registered networks
    suspend fun purchaseAirtime(
        purchaseAirtimeRequestPayload: PurchaseAirtimeRequestPayload
    ): JengaResponse? {
        return call<JengaResponse?, JengaResponse>(
            apiCall = {
                requestJengaAccessToken()?.let { jengaAccessTokenResponse ->
                    apis.equityJengaClient.purchaseAirtime(
                        authorization = "Bearer ${jengaAccessTokenResponse.accessToken}",
                        signature = jengaKeyService
                            .generateSignature(
                                jengaKeyService.signaturePayload(
                                    purchaseAirtimeRequestPayload.airtime.telco.name,
                                    purchaseAirtimeRequestPayload.airtime.amount,
                                    purchaseAirtimeRequestPayload.airtime.reference
                                )
                            ).toBase64(),
                        purchaseAirtimeRequestPayload = purchaseAirtimeRequestPayload
                    )
                }
            }
        )
    }
}