package com.entryventures.apis.equity.dto

import com.google.gson.annotations.SerializedName

class JengaAccessTokenResponse {
    @SerializedName("accessToken")
    lateinit var accessToken: String

    @SerializedName("refreshToken")
    lateinit var refreshToken: String

    @SerializedName("expiresIn")
    lateinit var expiresIn: String

    @SerializedName("issuedAt")
    lateinit var issuedAt: String

    @SerializedName("tokenType")
    lateinit var tokenType: String
}