package com.entryventures.seeders

import com.entryventures.extensions.asDate
import com.entryventures.models.PaymentMethod
import com.entryventures.models.jpa.*
import com.entryventures.repository.jpa.*
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class LoanSeeder(
    private val loanRepository: LoanRepository,
    private val userRepository: UserRepository,
    private val clientRepository: ClientRepository,
    private val loanOfficerRepository: LoanOfficerRepository,
    private val vaultRepository: VaultRepository,
    private val loanCollectionRepository: LoanCollectionRepository
) {

    @Bean
    fun seedLoans() {

        if(clientRepository.count() > 0) {
            return
        }

        val clientUsers: List<User> = listOf(
            User("John", "john_doe", "john@example.com", "1234567890"),
            User("Jane", "jane_smith", "jane@example.com", "0987654321"),
            User("Alice", "alice_wonder", "alice@example.com", "9876543210"),
            User("Bob", "bob_carter", "bob@example.com", "8765432109"),
            User("Eve", "eve_adams", "eve@example.com", "7654321098"),
        ).map { it.apply { passwordDigest = "password" } }

        val loanOfficerUsers: List<User> = listOf(
            User("Mike", "mike_jones", "mike@example.com", "6543210987"),
            User("Emily", "emily_brown", "emily@example.com", "5432109876"),
            User("David", "david_green", "david@example.com", "4321098765"),
            User("Olivia", "olivia_parker", "olivia@example.com", "3210987654"),
            User("Jack", "jack_white", "jack@example.com", "2109876543")
        ).map { it.apply { passwordDigest = "password" } }

        userRepository.saveAll(clientUsers)
        userRepository.saveAll(loanOfficerUsers)

        val clients: List<Client> = clientRepository.saveAll(clientUsers.map { usr -> Client().apply { user = usr; address = "123 Main Street, Dallas" } })
        val loanOfficers: List<LoanOfficer> = loanOfficerRepository.saveAll(loanOfficerUsers.map { usr -> LoanOfficer().apply { user = usr } })
        val vaults: List<Vault> = vaultRepository.saveAll(listOf(Vault("1236064542", "Kenya Commercial Bank"), Vault("2325432112", "Equity")))

        val loans = (1..50).map {
            val loan = Loan()
            loan.apply {
                amount = (5000..15000).random().toFloat()
                repaymentInterval = when((1..3).random()) {
                    1 -> ChronoUnit.DAYS
                    2 -> ChronoUnit.WEEKS
                    3 -> ChronoUnit.MONTHS
                    else -> ChronoUnit.YEARS
                }
                repaymentDuration = (1..5).random().toFloat()
                loan.firstRepaymentDate = LocalDate.of(2024, 4, 7).asDate()
                client = clients.random()
                loanOfficer = loanOfficers.random()
                vault = vaults.random()
            }
            loan
        }

        loanRepository.saveAll(loans)

        loans.forEach { savedLoan ->
            val collections = (1..(3..5).random()).map { LoanCollection().apply {
                amount = (500..800).random().toFloat()
                loan = savedLoan
                paymentMethod = PaymentMethod.entries.random()
                collectionDate = Date()
            } }
            loanCollectionRepository.saveAll(collections)
        }
    }
}