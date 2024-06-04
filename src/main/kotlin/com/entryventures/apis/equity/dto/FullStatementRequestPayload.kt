package com.entryventures.apis.equity.dto

import com.google.gson.annotations.SerializedName

data class FullStatementRequestPayload(
    @SerializedName("countryCode")
    var countryCode: String,

    @SerializedName("accountNumber")
    var accountNumber: String,

    @SerializedName("fromDate")
    var fromDate: String,

    @SerializedName("toDate")
    var toDate: String,

    @SerializedName("limit")
    var limit: Int = 10,

    @SerializedName("reference")
    var reference: String = "",

    @SerializedName("serial")
    var serial: String = "",

    @SerializedName("postedDateTime")
    var postedDateTime: String = "",

    @SerializedName("date")
    var date: String = "2023-10-13",

    @SerializedName("runningBalance")
    var runningBalance: RunningBalance = RunningBalance()
)

data class RunningBalance(
    @SerializedName("currency") var currency: String = "",
    @SerializedName("amount") var amount: Double = 0.0
)