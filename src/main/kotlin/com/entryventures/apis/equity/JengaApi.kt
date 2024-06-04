package com.entryventures.apis.equity

import com.entryventures.apis.equity.dto.*
import retrofit2.http.*

interface JengaApi {

    @POST("/authentication/api/v3/authenticate/merchant")
    suspend fun accessToken(
        @Header("Api-Key") apiKey: String,
        @Body payload: JengaAccessTokenRequestPayload,
        @Header("Content-Type") contentType: String = "application/json",
    ): JengaAccessTokenResponse

    @GET("/v3-apis/account-api/v3.0/accounts/balances/{countryCode}/{accountId}")
    suspend fun queryAccountBalance(
        @Header("Authorization") authorization: String,
        @Header("signature") signature: String,
        @Path("countryCode") countryCode: String,
        @Path("accountId") accountId: String,
    ): JengaResponse

    @GET("/v3-apis/account-api/v3.0/search/{countryCode}/{accountNumber}")
    suspend fun accountInquiry(
        @Header("Authorization") authorization: String,
        @Header("signature") signature: String,
        @Path("countryCode") countryCode: String,
        @Path("accountNumber") accountNumber: String,
    ): JengaResponse

    @POST("/v3-apis/account-api/v3.0/accounts/fullStatement")
    suspend fun accountFullStatement(
        @Header("Authorization") authorization: String,
        @Header("signature") signature: String,
        @Body fullStatementRequestPayload: FullStatementRequestPayload,
        @Header("Content-Type") contentType: String = "application/json",
    ): JengaResponse

    @GET("/v3-apis/account-api/v3.0/accounts/miniStatement/{countryCode}/{accountNumber}")
    suspend fun accountMiniStatement(
        @Header("Authorization") authorization: String,
        @Header("signature") signature: String,
        @Path("countryCode") countryCode: String,
        @Path("accountNumber") accountNumber: String,
    ): JengaResponse

    @POST("/v3-apis/account-api/v3.0/accounts/accountBalance/query")
    suspend fun accountOpeningAndClosingBalance(
        @Header("Authorization") authorization: String,
        @Header("signature") signature: String,
        @Body accountOpeningAndClosingRequestPayload: AccountOpeningAndClosingRequestPayload,
        @Header("Content-Type") contentType: String = "application/json",
    ): JengaResponse

    @POST("/v3-apis/transaction-api/v3.0/airtime")
    suspend fun purchaseAirtime(
        @Header("Authorization") authorization: String,
        @Header("signature") signature: String,
        @Body purchaseAirtimeRequestPayload: PurchaseAirtimeRequestPayload
    ): JengaResponse

    @POST("/v3-apis/transaction-api/v3.0/remittance/sendmobile")
    suspend fun sendToMobileWallet(
        @Header("Authorization") authorization: String,
        @Header("signature") signature: String,
        @Body sendToMobilePayload: JengaSendToMobileRequestPayload
    ): JengaResponse

    @POST("/v3-apis/transaction-api/v3.0/remittance/sendmobile")
    suspend fun receiveFromMobileWallet(
        @Header("Authorization") authorization: String,
        @Header("signature") signature: String,
        @Body receiveFromMobilePayload: JengaReceiveFromMobileRequestPayload
    ): JengaResponse
}