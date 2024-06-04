package com.entryventures.apis.equity.dto

import com.google.gson.annotations.SerializedName

data class AccountOpeningAndClosingRequestPayload(
    @SerializedName("countryCode") var countryCode: String,
    @SerializedName("accountId") var accountId: String,
    @SerializedName("date") var date: String
)