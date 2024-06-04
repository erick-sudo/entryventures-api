package com.entryventures.models

enum class LoanStatus(
    private var status: String
) {
    Pending("Pending"),
    Approved("Approved"),
    Waiting("Waiting"),
    Disbursing("Disbursed"),
    FailedDisbursement("FailedDisbursement"),
    FullyDisbursed("FullyDisbursed"),
    Closed("Closed");

    override fun toString(): String = status
}