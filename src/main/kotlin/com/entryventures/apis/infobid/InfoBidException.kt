package com.entryventures.apis.infobid

import com.google.gson.annotations.SerializedName

class InfoBidException {
    @SerializedName("requestError")
    lateinit var requestError: RequestError
}

class RequestError {
    @SerializedName("serviceException")
    lateinit var serviceException: ServiceException
}

class ServiceException {
    @SerializedName("messageId")
    lateinit var messageId: String

    @SerializedName("text")
    lateinit var text: String

}