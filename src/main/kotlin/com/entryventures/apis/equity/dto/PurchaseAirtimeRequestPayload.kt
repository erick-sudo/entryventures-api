package com.entryventures.apis.equity.dto

import com.google.gson.annotations.SerializedName

data class PurchaseAirtimeRequestPayload(
    @SerializedName("customer") var customer: Customer,
    @SerializedName("airtime") var airtime: Airtime
)

data class Customer(
    @SerializedName("countryCode") var countryCode: String,
    @SerializedName("mobileNumber") var mobileNumber: String
)

data class Airtime(
    @SerializedName("amount") var amount: String,
    @SerializedName("reference") var reference: String,
    @SerializedName("telco") var telco: Telco
)