package com.entryventures.models.dto

import com.entryventures.models.InterestType
import com.entryventures.models.LoanStatus
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.reflect.jvm.internal.ReflectProperties.Val

data class LoanDto(
    @JsonProperty("id") var id: String = "",
    @JsonProperty("status") var status: LoanStatus = LoanStatus.Pending,
    @JsonProperty("amount") var amount: Float,
    @JsonProperty("repayment_interval") var repaymentInterval: ChronoUnit,
    @JsonProperty("repayment_duration") var repaymentDuration: Float,
    @JsonProperty("first_repayment_date") var firstRepaymentDate: String,
    @JsonProperty("interest_rate") var interestRate: Float,
    @JsonProperty("type_of_interest") var typeOfInterest: InterestType,
    @JsonProperty("client_id") @NotBlank var clientId: String,
    @JsonProperty("loan_officer_id") @NotBlank var loanOfficerId: String,
    @JsonProperty("vault_id") @NotBlank var vaultId: String
)

data class LoanOut(
    @JsonProperty("id") var id: String = "",
    @JsonProperty("status") var status: LoanStatus = LoanStatus.Pending,
    @JsonProperty("amount") var amount: Float,
    @JsonProperty("repayment_interval") var repaymentInterval: ChronoUnit,
    @JsonProperty("repayment_duration") var repaymentDuration: Float,
    @JsonProperty("first_repayment_date") var firstRepaymentDate: String,
    @JsonProperty("interest_rate") var interestRate: Float,
    @JsonProperty("type_of_interest") var typeOfInterest: InterestType,
    @JsonProperty("client") var client: ClientOut,
    @JsonProperty("loan_officer") @NotBlank var loanOfficer: LoanOfficerOut,
    @JsonProperty("vault") @NotBlank var vault: VaultOut
)

data class ClientOut(
    @JsonProperty("id") var id: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("email") var email: String,
    @JsonProperty("phone") var phone: String
)

data class LoanOfficerOut(
    @JsonProperty("id") var id: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("email") var email: String,
    @JsonProperty("phone") var phone: String
)

data class VaultOut(
    @JsonProperty("id") var id: String,
    @JsonProperty("name") var name: String
)

data class LoanMiniStatement(
    @JsonProperty("loan") val loan: LoanOut,
    @JsonProperty("info") val info: LoanInfo
)