package com.entryventures.apis.mpesa.b2c

import com.google.gson.annotations.SerializedName

class SyncB2CRequestFailedResponse (
    @SerializedName("requestID") var requestID: String,
    @SerializedName("errorCode") var errorCode: String,
    @SerializedName("errorMessage") var errorMessage: String
)