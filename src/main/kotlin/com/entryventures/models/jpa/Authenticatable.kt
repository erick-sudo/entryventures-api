package com.entryventures.models.jpa

import org.springframework.security.core.GrantedAuthority

interface Authenticatable {
    val email: String
    val phone: String
    val passwordDigest: String
    fun authenticate(password: String): Boolean
    fun authorities(): MutableList<GrantedAuthority>
    val authorities: List<String>
}