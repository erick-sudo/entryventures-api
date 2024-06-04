package com.entryventures.trusted.mpesa

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import jakarta.persistence.*
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.UUID

@Document(collection = "b2c_results")
class B2CResult: Serializable {
    @Id
    @JsonProperty("Id")
    @SerializedName("Id")
    var id: String = UUID.randomUUID().toString()

    @JsonProperty("Result")
    @SerializedName("Result")
    lateinit var result: AsyncB2CResult

    @JsonProperty("Processed")
    @SerializedName("Processed")
    var processed: Boolean = false
}