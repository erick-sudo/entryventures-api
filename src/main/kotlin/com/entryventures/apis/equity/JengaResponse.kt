package com.entryventures.apis.equity

import com.google.gson.annotations.SerializedName

data class JengaResponse(
    @SerializedName("status") var status: Boolean,
    @SerializedName("code") var code: String,
    @SerializedName("message") var message: String,

    // Nullable fields
    @SerializedName("reference") var reference: String? = null,
    @SerializedName("data") var data: Any? = null,
    @SerializedName("transactionId") var transactionId: String? = null
)