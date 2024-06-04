package com.entryventures.apis.infobid.sms

import com.google.gson.annotations.SerializedName
import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*


@Document(collection = "sms_s")
data class SMSs(
    @Id @SerializedName("bulkId") var bulkId: String = UUID.randomUUID().toString(),
    @SerializedName("messages") var messages: List<SmsMessage>
)