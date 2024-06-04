package com.entryventures.exceptions

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class EntryVenturesExceptionResponse(
    @JsonProperty("ErrorCode") var errorCode: String,
    @JsonProperty("ErrorMessage") var errorMessage: String,
    @JsonProperty("ErrorDescription") var errorDescription: Any?
): Serializable
