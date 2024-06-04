package com.entryventures.crons

import com.entryventures.apis.mpesa.MpesaTransactions
import com.entryventures.models.EmploymentPosition
import com.entryventures.models.jpa.Salary
import com.entryventures.models.jpa.User
import com.entryventures.repository.jpa.LoanOfficerRepository
import com.entryventures.repository.jpa.SalaryRepository
import com.entryventures.repository.jpa.UserRepository
import com.entryventures.repository.jpa.VaultRepository
import com.entryventures.services.Crud
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SalaryProcessor(
    private val userRepository: UserRepository,
    private val mpesaTransactions: MpesaTransactions,
    private val vaultRepository: VaultRepository,
    private val salaryRepository: SalaryRepository,
    private val loanOfficerRepository: LoanOfficerRepository
) {

    @Scheduled(cron = "0 0 8 LW * *") // Every last weekday of the month
    fun loanOfficersPayout() {

        // Fetch Loan Officers
        val loanOfficers =

        runBlocking {
            TaskManagers.processCounterChannelTasks(
                inputs = loanOfficerRepository.findAll(),
                inputProcessor = { loanOfficer ->
                    mpesaTransactions.b2c(
                        transactionId = loanOfficer.id,
                        customer = loanOfficer.user,
                        amount = 45000f,
                        occasion = "Loan officer end month salary disbursement",
                        remarks = "Loan officer disbursement"
                    )
                    loanOfficer
                },
                outputReceiver = { loanOfficers ->
                    loanOfficers.forEach { loanOfficer ->
                        val salary = Salary().apply {
                            user = loanOfficer.user
                            amount = 1000f // loanOfficer.employment.salary
                            vault = Crud.find { TODO("Resolving right loan officer's salary vault") }
                        }

                        // Save salary information
                        salaryRepository.save(salary)
                    }
                }
            )
        }
    }

    @Scheduled(cron = "0 0 8 L * *") // Every last day of the month
    fun shareHoldersPayout() {

        // Fetch Loan Officers
        val shareHolders: List<User> = emptyList() // userRepository.findAllByPosition(EmploymentPosition.ShareHolder)

        runBlocking {
            TaskManagers.processCounterChannelTasks (
                inputs = shareHolders,
                inputProcessor = { user ->
                    mpesaTransactions.b2c(
                        transactionId = user.id,
                        customer = user,
                        amount = 90000f,
                        occasion = "Share holders end month salary disbursement",
                        remarks = "Share holder disbursement"
                    )
                    user
                },
                outputReceiver = { shareHolders ->
                    shareHolders.forEach { shareHolder ->
                        val salary = Salary().apply {
                            user = shareHolder
                            amount = 1000f // shareHolder.employment.salary
                            vault = Crud.find { TODO("Resolving right share holder's salary vault") }
                        }

                        // Save salary information
                        salaryRepository.save(salary)
                    }
                }
            )
        }
    }
}