package com.entryventures.apis.infobid.email

import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Encapsulates the standard electronic mail format
 */
class EmailPayload(
    @SerializedName("envelope") var envelope: EmailEnvelope,
    @SerializedName("header") var header: Map<String, String>,
    @SerializedName("body") var body: String
) {
    /**
     * Compiles a list of multipart form data parts from the headers and body
     * @return Returns a list of multipart form data parts
     */
    fun toParts(): List<MultipartBody.Part> {
        return (header + ("text" to body)).map { (key, value) -> MultipartBody.Part.createFormData(key, value) }
    }
}