package com.entryventures.apis.infobid.email

import com.google.gson.annotations.SerializedName

/**
 * Encapsulates an email envelope
 * @param from The sender
 * @param to The recipient
 */
class EmailEnvelope(
    @SerializedName("from") var from: String,
    @SerializedName("to") var to: String
)