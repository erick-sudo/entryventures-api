package com.entryventures.apis

import com.entryventures.apis.coop.CoopApi
import com.entryventures.apis.equity.JengaApi
import com.entryventures.apis.infobid.InfoBidApi
import com.entryventures.apis.infobid.InfoBidRequestInterceptor
import com.entryventures.apis.mpesa.MpesaApi
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException

@Service
class Apis {

    @Value("\${apis.mpesa.base-url}")
    lateinit var mpesaBaseUrl: String

    @Value("\${apis.jenga.base-url}")
    lateinit var jengaBaseUrl: String

    @Value("\${apis.co-op.base-url}")
    lateinit var coopBaseUrl: String

    @Value("\${apis.info-bid.base-url}")
    lateinit var infoBidBaseUrl: String

    val mpesaClient: MpesaApi by lazy {
        Retrofit.Builder()
            .baseUrl(mpesaBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(MpesaApi::class.java)
    }

    val equityJengaClient: JengaApi by lazy {
        Retrofit.Builder()
            .baseUrl(jengaBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(JengaApi::class.java)
    }

    val coopClient: CoopApi by lazy {
        Retrofit.Builder()
            .baseUrl(coopBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(CoopApi::class.java)
    }

    val infoBidClient: InfoBidApi by lazy {
        Retrofit.Builder()
            .baseUrl(infoBidBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(InfoBidRequestInterceptor()).build())
            .build()
            .create(InfoBidApi::class.java)
    }

    suspend fun <T, E> httpRequestWrapper(
        request: suspend () -> T,
        clientError: (HttpStatus, E?) -> Unit,
        serverError: (HttpStatus, E?) -> Unit,
        connectionError: () -> Unit,
        unknownHostError: () -> Unit
    ): T? {
        try {
            return request()
        } catch (e: ConnectException) {
            // Connection exception (network not available)
            connectionError()
        } catch (e: UnknownHostException) {
            // Unknown host exception (host not reachable)
            unknownHostError()
        } catch (e: HttpException) {
            // Http exceptions (non-successful-response)

            val responseBody = e.response()?.errorBody()

            try {
                // Check that response body is not null
                requireNotNull(responseBody)

                // Return the response body
                val responseString = responseBody.string()

                // Check that response body is not an empty string
                require(responseString.isNotEmpty())

                val mappedResponse = try {
                    jacksonObjectMapper().readValue(responseString, object : TypeReference<E>() {})
                } catch (bindException: Exception) {
                    null
                }

                if(e.code() in (400..< 500)) {
                    // Client errors
                    clientError(HttpStatus.valueOf(e.code()), mappedResponse)
                } else {
                    // Server errors
                    serverError(HttpStatus.valueOf(e.code()), mappedResponse)
                }
            } catch (ex: IllegalArgumentException) {
                // Server responses with empty body
                if(e.code() in (400..< 500)) {
                    // Client errors
                    clientError(HttpStatus.valueOf(e.code()), null)
                } else {
                    // Server errors
                    serverError(HttpStatus.valueOf(e.code()), null)
                }
            }
        }

        return null
    }
}