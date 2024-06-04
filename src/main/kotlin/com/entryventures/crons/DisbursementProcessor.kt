package com.entryventures.crons

import com.entryventures.apis.equity.JengaTransactions
import com.entryventures.apis.mpesa.MpesaTransactions
import com.entryventures.trusted.mpesa.B2CResult
import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.exceptions.ThirdPartyApiException
import com.entryventures.models.LoanStatus
import com.entryventures.models.jpa.Loan
import com.entryventures.trusted.jpa.LoanDisbursementSchedule
import com.entryventures.repository.jpa.LoanDisbursementScheduleRepository
import com.entryventures.repository.jpa.LoanRepository
import com.entryventures.repository.jpa.SyncB2CRequestAcceptanceResponseRepository
import com.entryventures.repository.mongo.B2CResultRepository
import com.entryventures.services.Crud
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class DisbursementProcessor(
    private val loanDisbursementScheduleRepository: LoanDisbursementScheduleRepository,
    private val loanRepository: LoanRepository,
    private val mpesaTransactions: MpesaTransactions,
    private val jengaTransactions: JengaTransactions,
    private val syncB2CRequestAcceptanceResponseRepository: SyncB2CRequestAcceptanceResponseRepository,
    private val b2CResultRepository: B2CResultRepository
) {

    /**
     * Process loan disbursements from disbursement queue
     * When a loan is approved, it gets sent to the [disbursement-schedules] queue
     * and gets received here for processing
     */
    @JmsListener(destination = "disbursement-schedules")
    fun initiateDisbursements(loanDisbursementSchedule: LoanDisbursementSchedule) {
        runBlocking {
            processB2CLoanDisbursementTransaction(loanDisbursementSchedule)?.apply {
                    // Switch loan status to DISBURSED
                    status = LoanStatus.Disbursing
                    // Persist to database
                    loanRepository.save(this)
                }
        }
    }

    /**
     * A worker coroutine - Initiates loan disbursement
     * @param loanDisbursementSchedule : LoanDisbursementSchedule - Scheduled disbursement awaiting processing
     */
    suspend fun processB2CLoanDisbursementTransaction(
        loanDisbursementSchedule: LoanDisbursementSchedule
    ): Loan? {

        return try {
            // Process Business to Client Transaction
            when(loanDisbursementSchedule.loan.vault.name) {
                "MPESA" -> mpesaTransactions.b2c(
                    transactionId = "LOAN#${loanDisbursementSchedule.loan.id}",
                    customer = loanDisbursementSchedule.loan.client.user,
                    amount = loanDisbursementSchedule.loan.amount,
                    occasion = "Loan Disbursement",
                    remarks = "Loan Disbursement"
                )?.let { succesfulB2CResponse ->

                    // Persist successful transaction details
                    syncB2CRequestAcceptanceResponseRepository.save(succesfulB2CResponse)
                    loanDisbursementSchedule.loan
                }

//                "EQUITY" -> jengaTransactions.sendToMobileWallet(
//                    JengaSendToMobileRequestPayload()
//                ).let {
//                    loanDisbursementSchedule.loan
//                }

                else -> null
            }

        } catch (ex: ThirdPartyApiException) {
            withContext(Dispatchers.IO) {
                loanRepository.save(loanDisbursementSchedule.loan.apply {
                    status = LoanStatus.Waiting
                })
            }
            null
        } catch (ex: EntryVenturesException) {
            withContext(Dispatchers.IO) {
                loanRepository.save(loanDisbursementSchedule.loan.apply {
                    status = LoanStatus.Waiting
                })
            }
            null
        }
    }


    /**
     * Process recorded b2c callbacks and update loan status to full disbursement
     * A recorded b2c callback will be accessed for processing within ten minutes after record
     */
    @JmsListener(destination = "successful-mpesa-b2c-callbacks")
    fun processSuccessfulB2CLoanDisbursements(b2cResult: B2CResult) {

        // Extract loan id
        val loanId = b2cResult.result.originatorConversationId.split("#").let { if(it.size > 1) it[1] else "" }

        try {
            Crud.find {
                loanRepository.findById(loanId)
            }.also { loan ->
                // Set the loan status to full disbursement
                if(b2cResult.result.resultParameters!=null) {
                    loan.status = LoanStatus.FullyDisbursed
                } else {
                    loan.status = LoanStatus.FailedDisbursement
                }

                // Persist the status change
                loanRepository.save(loan)

                // Set the b2cResult to processed and persist
                b2cResult.processed = true
                b2CResultRepository.save(b2cResult)
            }
        } catch (e: EntryVenturesException) {
            // Handle loan not found
            println("Loan Not Found: $loanId")
        }
    }
}