package com.entryventures.models.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LoanInfo(
    @JsonProperty("loan_period") val loanPeriod: String,
    @JsonProperty("installment_pattern") val installmentPattern: String,
    @JsonProperty("expected_amount_to_date") val expectedAmountToDate: Float,
    @JsonProperty("calculated_amount_to_date") val calculatedAmountToDate: Float,
    @JsonProperty("elapsed_periods") val elapsedPeriods: Long,
    @JsonProperty("current_period") val currentPeriod: Float,
    @JsonProperty("elapsed_days") val elapsedDays: Long,
    @JsonProperty("collected_amount") val collectedAmount: Float,
    @JsonProperty("defaulted") val defaulted: Boolean,
    @JsonProperty("fine") val fine: Float,
    @JsonProperty("balance") val balance: Float,
    @JsonProperty("total") val total: Float
)
