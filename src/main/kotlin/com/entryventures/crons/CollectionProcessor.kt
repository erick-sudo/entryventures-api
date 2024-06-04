package com.entryventures.crons

import com.entryventures.apis.infobid.InfoBidRequests
import com.entryventures.apis.infobid.sms.SMSs
import com.entryventures.apis.infobid.sms.SmsDestination
import com.entryventures.apis.infobid.sms.SmsMessage
import com.entryventures.apis.mpesa.express.AsyncExpressBody
import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.exceptions.ThirdPartyApiException
import com.entryventures.extensions.toCurrency
import com.entryventures.models.LoanStatus
import com.entryventures.models.jpa.LoanCollection
import com.entryventures.models.jpa.LoanService
import com.entryventures.repository.jpa.LoanCollectionRepository
import com.entryventures.repository.jpa.SyncMpesaExpressResponseRepository
import com.entryventures.repository.mongo.SMSsRepository
import com.entryventures.services.Crud
import kotlinx.coroutines.runBlocking
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

/**
 * Oversees all active loans and updates relevant person on the required actions.
 * Loan statuses are also managed by triggers that may be as a result of:
 *  - payment of installments
 *  - overdue payments
 *  - loan applications
 *  - loan approvals
 *  - loan disbursements
 */
@Component
class CollectionProcessor(
    private val infoBidRequests: InfoBidRequests,
    private val smSsRepository: SMSsRepository,
    private val syncMpesaExpressResponseRepository: SyncMpesaExpressResponseRepository,
    private val loanCollectionRepository: LoanCollectionRepository,
    private val loanService: LoanService
) {

    //@Scheduled(cron = "0 0 8 * * MON-FRI") // Every week day at 8 O'Clock
    //@Scheduled(fixedRate = 5000)
    fun checkMissedRepayments() {

        // Filter for defaulted collections and send notifications
        loanService.getLoansWithCollectionsByStatus(LoanStatus.FullyDisbursed).filter { loan -> loan.defaulted() }.forEach { loan ->

            val sms = SMSs(
                messages = listOf(
                    SmsMessage(
                        from = "ENTRY VENTURES",
                        destinations = listOf(
                            SmsDestination(
                                messageId = loan.client.id,
                                to = loan.client.user.phone
                            )
                        ),
                        text = loan.defaultMessage()
                    )
                )
            )

            runBlocking {
                try {
                    infoBidRequests.sendSms(
                        sms
                    )?.apply {
                        // Record successfully sent sms
                        smSsRepository.save(sms)
                    }
                } catch (ex: ThirdPartyApiException) {
                    // Error sending sms
                } catch (ex: EntryVenturesException) {
                    // Error sending sms
                }
            }
        }
    }

    /**
     * Read mpesa express callbacks from queue
     */
    @JmsListener(destination = "successful-mpesa-express-callbacks")
    fun processLoanRepaymentStkCallbacks(asyncExpressBody: AsyncExpressBody) {

        // Check if the transaction was successful
        if(asyncExpressBody.body.stkCallback.callbackMetadata != null) {

            try {
                val successExpress = Crud.find {
                    syncMpesaExpressResponseRepository.findByCheckoutRequestId(asyncExpressBody.body.stkCallback.checkoutRequestId)
                }

                try {
                    val ln = loanService.getLoanWithCollections(successExpress.reference ?: "")

                    val collectionAmount = try {
                        var a = 0f
                        asyncExpressBody.body.stkCallback.callbackMetadata!!.item!!.forEach {
                            if(it.name == "Amount") {
                                a = it.value.toFloat()
                            }
                        }
                        a
                    } catch (ex: NumberFormatException) {
                        0f
                    }

                    if(collectionAmount > 0f) {
                        // Construct a loan collection
                        val collection = LoanCollection().apply {
                            amount = collectionAmount
                            loan = ln
                        }

                        loanCollectionRepository.save(collection)

                        runBlocking {
                            // Acknowledge payment with an SMS
                            infoBidRequests.sendSms(
                                SMSs(
                                    messages = listOf(
                                        SmsMessage(
                                            from = "EVentures",
                                            destinations = listOf(
                                                SmsDestination(
                                                    messageId = ln.client.id,
                                                    to = ln.client.user.phone,
                                                )
                                            ),
                                            text = "Dear ${ln.client.user.name}, a loan repayment of ${collectionAmount.toDouble().toCurrency()} has been received. Your balance is ${ln.balance().toDouble().toCurrency()}"
                                        )
                                    )
                                )
                            )
                        }
                    }
                } catch (ex: EntryVenturesException) {
                    // Handle Loan not found
                }
            } catch (ex: EntryVenturesException) {
                // Handle SyncMpesaExpressResponse not found
            }

            //val loan = Crud.find { loanRepository.findById(asyncExpressBody) }
        } else {
            // Handle unsuccessful transaction
        }
    }
}