package com.entryventures.repository.jpa

import com.entryventures.models.jpa.Client
import com.entryventures.models.jpa.User
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ClientRepository: JpaRepository<Client, String> {

//    fun existsByEmailOrPhone(email: String, phone: String): Boolean
//
//    @Transactional
//    @Query("SELECT c FROM Client c WHERE c.email = :identifier OR c.phone = :identifier")
//    fun findByEmailOrPhone(identifier: String): Optional<Client>

    @Transactional
    @Query("SELECT c FROM Client c WHERE c.user.name ILIKE %:q% OR c.user.userName ILIKE %:q% OR c.user.email ILIKE %:q% OR c.user.phone ILIKE %:q%")
    fun searchClients(q: String): List<Client>
}