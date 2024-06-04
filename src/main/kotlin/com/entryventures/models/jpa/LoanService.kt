package com.entryventures.models.jpa

import com.entryventures.models.LoanStatus
import com.entryventures.repository.jpa.LoanRepository
import com.entryventures.services.Crud
import jakarta.transaction.Transactional
import org.hibernate.Hibernate
import org.springframework.stereotype.Service

@Service
class LoanService(
    private val loanRepository: LoanRepository
) {

    @Transactional
    fun getLoanWithCollections(loanId: String): Loan {
        val loan = Crud.find { loanRepository.findById(loanId) }
        Hibernate.initialize(loan.loanCollections)
        return loan
    }

    @Transactional
    fun getLoansWithCollectionsByStatus(loanStatus: LoanStatus): List<Loan> {
        val loans = loanRepository.findAllByStatus(loanStatus)

        loans.forEach { Hibernate.initialize(it.loanCollections) }

        return loans
    }
}