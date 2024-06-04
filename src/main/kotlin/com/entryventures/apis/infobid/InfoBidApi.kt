package com.entryventures.apis.infobid

import com.entryventures.apis.infobid.response.InfoBidSuccessResponse
import com.entryventures.apis.infobid.sms.SMSs
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface InfoBidApi {

    /**
     * Send a sms via the INFO_BID api
     * @param contentType Content-Type of the sms request payload
     * @param smsRequestPayload The payload of the sms endpoint
     */
    @POST("sms/2/text/advanced")
    suspend fun sendSMS(@Header("Content-Type") contentType: String, @Body smsRequestPayload: SMSs): InfoBidSuccessResponse

    @Multipart
    @POST("email/3/send")
    suspend fun sendMail(
        @Part parts: List<MultipartBody.Part>
    ): InfoBidSuccessResponse

    @POST("whatsapp/1/message/template")
    suspend fun sendWhatsapp()
}