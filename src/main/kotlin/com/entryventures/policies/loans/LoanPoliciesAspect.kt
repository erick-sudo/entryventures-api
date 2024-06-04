package com.entryventures.policies.loans

import com.entryventures.exceptions.EntryVenturesPolicyException
import com.entryventures.models.jpa.Loan
import com.entryventures.repository.jpa.LoanRepository
import com.entryventures.repository.jpa.UserRepository
import com.entryventures.services.Crud
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Policies to be met or evaluated concerning different loan actions
 */
@Aspect
class LoanPoliciesAspect(
    private val loanRepository: LoanRepository,
    private val userRepository: UserRepository
) {

    fun create() {

    }

    fun update() {

    }

    fun delete() {

    }

    @Before("@annotation(LoanOpeningPolicy)")
    fun loanOpeningPolicy(jointPoint: JoinPoint) {

//        val loan = findLoan(requireLoanId(jointPoint))
//
//        throw EntryVenturesPolicyException(HttpStatus.FORBIDDEN) {
//            mapOf(
//                "message" to "Loan opening policies violated",
//                "violations" to listOf(
//                    "Invalid loan amount",
//                    "Client not found"
//                )
//            )
//        }
    }

    @Before("@annotation(LoanApprovalPolicy)")
    fun loanApprovalPolicy() {

    }

    @Before("@annotation(LoanDisbursementPolicy)")
    fun loanDisbursementPolicy() {

    }

    @Before("@annotation(InstallmentCollectionPolicy)")
    fun installmentCollectionPolicy() {

    }

    @Before("@annotation(LoanClosurePolicy)")
    fun loanClosurePolicy() {

    }

    @Before("@annotation(LoanNotificationPolicy)")
    fun loanNotificationPolicy() {

    }

    @Before("@annotation(LoanAccessPolicy)")
    fun loanAccessPolicy() {

    }

    /**
     * Extracts loan id from policy resolution context
     * @param jointPoint loan access joint point
     */
    private fun requireLoanId(jointPoint: JoinPoint): String {

        // Check if the target includes at least the first argument as the loan id
        if(jointPoint.args.size != 1) {
            // No argument detected for a possible loan id
            throw EntryVenturesPolicyException(HttpStatus.FORBIDDEN) {
                mapOf(
                    "status" to "Missing Parameter",
                    "error" to "A loan id not detected"
                )
            }
        }

        // Check if the expected loan id first argument is a String
        if(jointPoint.args[0] is String) {
            return jointPoint.args[0].toString()
        } else {
            // Unexpected data type for loan id
            throw EntryVenturesPolicyException(HttpStatus.FORBIDDEN) {
                mapOf(
                    "status" to "Invalid Parameter",
                    "error" to "Not a valid loan id: required ${String::class.simpleName} found ${jointPoint.args[0].javaClass.simpleName}"
                )
            }
        }
    }


    /**
     * Finds a loan from the loan repository
     * @param loanId Primary key value of the loan
     */
    private fun findLoan(loanId: String): Loan {
        return Crud.find {
            loanRepository.findById(loanId)
        }
    }

    /**
     * Determine if current user has access to a specific loan
     */
    private fun read(loan: Loan) {
        val authentication = SecurityContextHolder.getContext().authentication
        val user = Crud.find {
            userRepository.findByUsernameOrEmail(authentication.principal.toString())
        }
        val authorities = authentication.authorities.map { it.authority }

    }
}