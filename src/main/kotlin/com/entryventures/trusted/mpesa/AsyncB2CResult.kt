package com.entryventures.trusted.mpesa

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Represent whether a B2C request was successful or not
 */
class AsyncB2CResult: Serializable {
    @SerializedName("ResultType")
    @JsonProperty("ResultType")
    var resultType: Int = 0

    @SerializedName("ResultCode")
    @JsonProperty("ResultCode")
    var resultCode: Int = 0

    @SerializedName("ResultDesc")
    @JsonProperty("ResultDesc")
    lateinit var resultDesc: String

    @SerializedName("OriginatorConversationID")
    @JsonProperty("OriginatorConversationID")
    lateinit var originatorConversationId: String

    @SerializedName("ConversationID")
    @JsonProperty("ConversationID")
    lateinit var conversationId: String

    @SerializedName("TransactionID")
    @JsonProperty("TransactionID")
    lateinit var transactionId: String

    @SerializedName("ResultParameters")
    @JsonProperty("ResultParameters")
    var resultParameters: ResultParameter? = null

    @SerializedName("ReferenceData")
    @JsonProperty("ReferenceData")
    lateinit var referenceData: ReferenceData
}