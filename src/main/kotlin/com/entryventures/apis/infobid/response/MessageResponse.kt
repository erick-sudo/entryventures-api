package com.entryventures.apis.infobid.response

import com.google.gson.annotations.SerializedName

class MessageResponse {
    @SerializedName("messageId")
    lateinit var messageId: String

    @SerializedName("status")
    lateinit var status: MessageStatus

    @SerializedName("to")
    lateinit var to: String
}
