package com.entryventures.exceptions

import org.springframework.http.HttpStatus

class EntryVenturesPolicyException(
    val serverStatus: HttpStatus,
    var policyViolations: () -> Any
): Exception() {
    override val message: String
        get() = "Policy Violation; ${policyViolations()}"
}