package com.entryventures.exceptions

import org.springframework.http.HttpStatus

class ThirdPartyApiException(
    val status: HttpStatus,
    val party: String,
    val errorMessage: String,
    var exceptionDescription: () -> Any?
): Exception() {
    override val message: String
        get() = "$party; $errorMessage"
}