package com.entryventures.dtos

import com.google.gson.annotations.SerializedName

class MessageDto {
    @SerializedName("phones")
    lateinit var phones: List<Long>

    @SerializedName("text")
    lateinit var text: String
}