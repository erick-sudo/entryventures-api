package com.entryventures.apis.infobid.response

import com.google.gson.annotations.SerializedName

/**
 * A successful infobid api's sms or email send initiations
 */
class InfoBidSuccessResponse {
    @SerializedName("bulkId")
    lateinit var bulkId: String

    @SerializedName("messages")
    lateinit var messages: List<MessageResponse>
}