package com.entryventures.apis.equity.dto

import com.google.gson.annotations.SerializedName

data class JengaSendToMobileRequestPayload(
    @SerializedName("source") var source: MoveMoneySource,
    @SerializedName("destination") var destination: MoveMoneyDestination,
    @SerializedName("transfer") var transfer: MoneyTransfer
)

data class MoveMoneySource(
    @SerializedName("countryCode") var countryCode: String,
    @SerializedName("name") var name: String,
    @SerializedName("accountNumber") var accountNumber: String,
)

data class MoveMoneyDestination(
    @SerializedName("type") var type: String,
    @SerializedName("countryCode") var countryCode: String,
    @SerializedName("name") var name: String,
    @SerializedName("mobileNumber") var mobileNumber: String,
    @SerializedName("walletName") var walletName: String
)

data class MoneyTransfer(
    @SerializedName("type") var type: String,
    @SerializedName("amount") var amount: String,
    @SerializedName("currencyCode") var currencyCode: String,
    @SerializedName("reference") var reference: String,
    @SerializedName("date") var date: String,
    @SerializedName("description") var description: String,
    @SerializedName("callbackUrl") var callbackUrl: String
)