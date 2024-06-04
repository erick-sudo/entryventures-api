package com.entryventures.apis.infobid.response

import com.google.gson.annotations.SerializedName

/**
 * Status of a sms or email send initiation
 */
class MessageStatus {
    @SerializedName("description")
    lateinit var description: String

    @SerializedName("groupId")
    var groupId: Int = 0

    @SerializedName("groupName")
    lateinit var groupName: String

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    lateinit var name: String
}