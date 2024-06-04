package com.entryventures.apis.infobid.sms

import com.google.gson.annotations.SerializedName

/**
 * Represents a verified phone number to receive a sms
 */
class SmsDestination(
    @SerializedName("messageId") var messageId: String,
    @SerializedName("to") var to: String
)