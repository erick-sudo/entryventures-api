package com.entryventures.repository.jpa

import com.entryventures.models.EmploymentPosition
import com.entryventures.models.jpa.User
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository : JpaRepository<User, String> {

    @Transactional
    fun findByUserName(username: String?): Optional<User>

    @Transactional
    fun findByEmail(email: String?): User?

    @Transactional
    @Query("SELECT u FROM User u WHERE u.userName = :identifier OR u.email = :identifier")
    fun findByUsernameOrEmail(identifier: String): Optional<User>
}