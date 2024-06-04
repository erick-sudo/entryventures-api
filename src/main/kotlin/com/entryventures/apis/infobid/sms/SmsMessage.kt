package com.entryventures.apis.infobid.sms

import com.google.gson.annotations.SerializedName

/**
 * Holds the message, the sender and the destinations of a message
 */
class SmsMessage (
    @SerializedName("from") var from: String,
    @SerializedName("destinations") var destinations: List<SmsDestination>,
    @SerializedName("text") var text: String
)