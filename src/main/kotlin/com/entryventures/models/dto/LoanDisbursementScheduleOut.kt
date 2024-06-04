package com.entryventures.models.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class LoanDisbursementScheduleOut(
    @JsonProperty("id") var id: String,
    @JsonProperty("client") var client: String,
    @JsonProperty("amount") var amount: Float,
    @JsonProperty("processed") var processed: Boolean,
    @JsonProperty("created_at") var createdAt: Date,
    @JsonProperty("processed_at") var processedAt: Date?
)
