package com.entryventures.trusted.mpesa

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ReferenceItem: Serializable {
    @SerializedName("Key")
    @JsonProperty("Key")
    var key: String = ""

    @SerializedName("Value")
    @JsonProperty("Value")
    var value: String = ""
}