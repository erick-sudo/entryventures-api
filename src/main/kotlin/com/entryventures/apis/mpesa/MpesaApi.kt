package com.entryventures.apis.mpesa

import com.entryventures.apis.mpesa.b2c.B2CRequestPayload
import com.entryventures.apis.mpesa.b2c.SyncB2CRequestAcceptanceResponse
import com.entryventures.apis.mpesa.express.StkRequestPayload
import com.entryventures.apis.mpesa.express.SyncMpesaExpressResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

private const val AUTHORIZATION_ENDPOINT = "oauth/v1/generate?grant_type=client_credentials"
private const val B2C_ENDPOINT = "mpesa/b2c/v3/paymentrequest"
private const val C2B_ENDPOINT = "mpesa/c2b/v1/registerurl"
private const val STK_EXPRESS = "mpesa/stkpush/v1/processrequest"

interface MpesaApi {

    // Mpesa Authorization api
    @GET(AUTHORIZATION_ENDPOINT)
    suspend fun accessToken(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "application/json"
    ): MpesaAccessTokenResponse

    // Mpesa Express Stk push
    @POST(STK_EXPRESS)
    suspend fun mpesaExpress(
        @Header("Authorization") authorization: String,
        @Body payload: StkRequestPayload,
        @Header("Accept") accept: String = "application/json"
    ): SyncMpesaExpressResponse

    // Business to Client endpoint
    @POST(B2C_ENDPOINT)
    suspend fun b2c(
        @Header("Authorization") authorization: String,
        @Body payload: B2CRequestPayload,
        @Header("Accept") accept: String = "application/json"
    ): SyncB2CRequestAcceptanceResponse
}