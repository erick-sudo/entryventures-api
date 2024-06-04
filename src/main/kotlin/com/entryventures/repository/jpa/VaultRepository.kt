package com.entryventures.repository.jpa

import com.entryventures.models.jpa.Vault
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VaultRepository: JpaRepository<Vault, String> {
    @Transactional
    @Query("SELECT v FROM Vault v WHERE v.name ILIKE %:q% OR v.key ILIKE %:q%")
    fun searchVaults(q: String): List<Vault>
}