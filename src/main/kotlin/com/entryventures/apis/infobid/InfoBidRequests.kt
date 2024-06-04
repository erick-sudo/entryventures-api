package com.entryventures.apis.infobid

import com.entryventures.apis.ApiCallWrapper
import com.entryventures.apis.Apis
import com.entryventures.apis.infobid.email.EmailPayload
import com.entryventures.apis.infobid.response.InfoBidSuccessResponse
import com.entryventures.apis.infobid.sms.SMSs
import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.exceptions.ThirdPartyApiException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class InfoBidRequests(
    private val apis: Apis
) : ApiCallWrapper {

    override suspend fun <P, E> call(apiCall: suspend () -> P): P? {
        return apis.httpRequestWrapper<P, E> (
            request = { apiCall() },
            clientError = { statusCode, resBody ->
                throw EntryVenturesException(
                    serverStatus = statusCode,
                    errorMessage = { "Client Error" },
                    errorDescription = { resBody }
                )
            },
            serverError = { statusCode, resBody ->
                throw ThirdPartyApiException(
                    status = statusCode,
                    party = "INFOBID",
                    errorMessage = "Internal server Error",
                    exceptionDescription = {
                        mapOf(
                            "status" to statusCode,
                            "message" to "INFOBID api encountered an internal server error. Please try again later",
                            "error" to resBody
                        )
                    }
                )
            },
            connectionError = {
                throw ThirdPartyApiException(
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                    party = "INFOBID",
                    errorMessage = "Connection Error",
                    exceptionDescription = {
                        "Could not reach server"
                    }
                )
            },
            unknownHostError = {
                throw ThirdPartyApiException(
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                    party = "INFOBID",
                    errorMessage = "UnknownHostException",
                    exceptionDescription = {
                        "Host not reachable"
                    }
                )
            }
        )
    }

    suspend fun sendMail(emailPayload: EmailPayload): InfoBidSuccessResponse? {
        return call<InfoBidSuccessResponse?, Map<String, Any>?> {
            apis.infoBidClient.sendMail(
                parts = emailPayload.toParts()
            )
        }
    }

    suspend fun sendSms(messages: SMSs): InfoBidSuccessResponse? {
        return call<InfoBidSuccessResponse?, Map<String, Any>?> {
            apis.infoBidClient.sendSMS(
                contentType = "application/json",
                smsRequestPayload = messages
            )
        }
    }
}