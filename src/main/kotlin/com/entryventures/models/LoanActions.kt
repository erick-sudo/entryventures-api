package com.entryventures.models

enum class LoanActions(val description: String ) {
    Open("Make a new loan application"),
    Approve("Approve a verified loan application"),
    Disburse("Disburse an approved loan"),
    CollectInstallment("Collect a loan installment"),
    Close("Close an approved loan"),
    NotifyAnOverdueInstallment("Send a notification for an overdue loan")
}