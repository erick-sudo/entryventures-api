package com.entryventures.apis.mpesa.b2c

import com.google.gson.annotations.SerializedName
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "processed_b2c")
class SyncB2CRequestAcceptanceResponse {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    var id: String = UUID.randomUUID().toString()

    @SerializedName("ConversationID")
    lateinit var conversationId: String

    @SerializedName("OriginatorConversationID")
    lateinit var originatorConversationId: String

    @SerializedName("ResponseCode")
    lateinit var responseCode: String

    @SerializedName("ResponseDescription")
    lateinit var responseDescription: String

    @SerializedName("Date")
    var createdAt: Date = Date()
}