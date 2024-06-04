package com.entryventures.trusted.mpesa

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReferenceData(
    @JsonProperty("ReferenceItem")
    @SerializedName("ReferenceItem")
    var referenceItem: ReferenceItem
): Serializable