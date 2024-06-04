package com.entryventures.apis.mpesa.express

import com.google.gson.annotations.SerializedName
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "sync_mpesa_success_responses")
data class SyncMpesaExpressResponse (
    @SerializedName("") @Id var id: String = UUID.randomUUID().toString(),
    @SerializedName("MerchantRequestID") var merchantRequestId: String,
    @SerializedName("CheckoutRequestID") var checkoutRequestId: String,
    @SerializedName("ResponseCode") var responseCode: String,
    @SerializedName("ResponseDescription") var responseDescription: String,
    @SerializedName("CustomerMessage") var customerMessage: String,
    @SerializedName("Reference") @Column(nullable = false) var reference: String?
)