package com.entryventures.trusted.mpesa

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResultParameter: Serializable {
    @SerializedName("ResultParameter")
    @JsonProperty("ResultParameter")
    var resultParameter: List<ReferenceItem> = listOf()
}