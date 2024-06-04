package com.entryventures.repository.jpa

import com.entryventures.models.jpa.LoanOfficer
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LoanOfficerRepository: JpaRepository<LoanOfficer, String> {
    @Transactional
    @Query("SELECT lo FROM LoanOfficer lo WHERE lo.user.name ILIKE %:q% OR lo.user.userName ILIKE %:q% OR lo.user.email ILIKE %:q% OR lo.user.phone ILIKE %:q%")
    fun searchLoanOfficers(q: String): List<LoanOfficer>
}