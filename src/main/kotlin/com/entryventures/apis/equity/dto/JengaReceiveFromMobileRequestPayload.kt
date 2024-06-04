package com.entryventures.apis.equity.dto

import com.google.gson.annotations.SerializedName

data class JengaReceiveFromMobileRequestPayload(
    @SerializedName("source") var source: MoveMoneySource,
    @SerializedName("destination") var destination: MoveMoneyDestination,
    @SerializedName("transfer") var transfer: MoneyTransfer
)