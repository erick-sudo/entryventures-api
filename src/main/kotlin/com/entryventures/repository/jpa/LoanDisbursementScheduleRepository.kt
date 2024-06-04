package com.entryventures.repository.jpa

import com.entryventures.trusted.jpa.LoanDisbursementSchedule
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LoanDisbursementScheduleRepository: JpaRepository<LoanDisbursementSchedule, String> {

    @Query("SELECT ld FROM LoanDisbursementSchedule ld WHERE ld.processed = false AND ld.loan.status = 1")
    fun findUnprocessedLoanDisbursementSchedules(pageable: Pageable): List<LoanDisbursementSchedule>

    @Query("SELECT ld FROM LoanDisbursementSchedule ld ORDER BY ld.updatedAt DESC")
    fun findMostRecentlyModified(pageable: Pageable): List<LoanDisbursementSchedule>
}