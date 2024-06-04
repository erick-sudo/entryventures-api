package com.entryventures.apis.mpesa.express

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.UUID

@Document(collection = "mpesa_express_callbacks")
data class AsyncExpressBody(
    @Id var id: String = UUID.randomUUID().toString(),
    @JsonProperty("Body") var body: ExpressResultBody
): Serializable

data class ExpressResultBody(
    @JsonProperty("stkCallback") var stkCallback: StkCallback
): Serializable

data class StkCallback(
    @JsonProperty("MerchantRequestID") var merchantRequestId: String,
    @JsonProperty("CheckoutRequestID") var checkoutRequestId: String,
    @JsonProperty("ResultCode") var resultCode: String,
    @JsonProperty("ResultDesc") var resultDesc: String,
    @JsonProperty("CallbackMetadata") var callbackMetadata: CallbackMetadataItem?
): Serializable

data class CallbackMetadataItem(
    @JsonProperty("Item") var item: List<SuccessExpressMetadataItem>?
): Serializable

data class SuccessExpressMetadataItem(
    @JsonProperty("Name") var name: String,
    @JsonProperty("Value") var value: String
): Serializable