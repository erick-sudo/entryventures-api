package com.entryventures.models.dto

import com.entryventures.models.EmploymentPosition
import com.entryventures.models.jpa.User
import com.fasterxml.jackson.annotation.JsonProperty

data class UserDto(
    @JsonProperty("id") var id: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("user_name") var userName: String,
    @JsonProperty("email") var email: String,
    @JsonProperty("phone") var phone: String,
    @JsonProperty("authorities") var authorities: List<String>
) {

    constructor(user: User): this(
        id = user.id,
        name = user.name,
        userName = user.userName,
        email = user.email,
        phone = user.phone,
        authorities = user.authorities
    )
}