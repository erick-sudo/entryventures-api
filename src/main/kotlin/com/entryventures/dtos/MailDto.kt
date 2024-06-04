package com.entryventures.dtos

import com.google.gson.annotations.SerializedName
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class MailDto {
    @SerializedName("from")
    @Email
    @NotBlank
    lateinit var from: String

    @SerializedName("to")
    @Email
    @NotBlank
    lateinit var to: String

    @SerializedName("subject")
    @NotBlank
    lateinit var subject: String

    @SerializedName("text")
    @NotBlank
    lateinit var text: String
}