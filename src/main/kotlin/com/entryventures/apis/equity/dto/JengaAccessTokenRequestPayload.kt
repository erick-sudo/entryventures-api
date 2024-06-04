package com.entryventures.apis.equity.dto

import com.google.gson.annotations.SerializedName

data class JengaAccessTokenRequestPayload(
    @SerializedName("merchantCode") var merchantCode: String,
    @SerializedName("consumerSecret") var consumerSecret: String
)