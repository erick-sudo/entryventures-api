package com.entryventures.models.jpa
import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.extensions.toDate
import com.entryventures.extensions.toLocalDate
import com.entryventures.extensions.yyyymmdd
import com.entryventures.models.InterestType
import com.entryventures.models.LoanStatus
import com.entryventures.models.dto.LoanDto
import com.entryventures.models.dto.LoanInfo
import com.entryventures.models.dto.LoanMiniStatement
import com.entryventures.models.dto.LoanOut
import com.entryventures.repository.jpa.ClientRepository
import com.entryventures.repository.jpa.LoanOfficerRepository
import com.entryventures.repository.jpa.UserRepository
import com.entryventures.repository.jpa.VaultRepository
import com.entryventures.services.Crud
import com.entryventures.trusted.jpa.LoanDisbursementSchedule
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.GenericGenerator
import org.springframework.http.HttpStatus
import java.io.Serializable
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.math.floor


@Entity
@Table(name = "loans")
class Loan(): Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String

    @JsonProperty("amount")
    @Column(name = "amount", nullable = false)
    var amount: Float = 0f

    @JsonProperty("repayment_interval")
    @Column(name = "repayment_interval", nullable = false)
    var repaymentInterval: ChronoUnit = ChronoUnit.WEEKS

    @JsonProperty("repayment_duration")
    @Column(name = "repayment_duration", nullable = false)
    var repaymentDuration: Float = 1f

    @JsonProperty("first_repayment_date")
    @Column(name = "first_repayment_date")
    lateinit var firstRepaymentDate: Date

    @JsonProperty("interest_rate")
    @Column(name = "interest_rate", nullable = false)
    var interestRate: Float = 0.1f

    @JsonProperty("type_of_interest")
    @Column(name = "type_of_interest", nullable = false)
    var typeOfInterest: InterestType = InterestType.SIMPLE

    @Column(name = "status", nullable = false)
    @JsonProperty("status")
    @NotNull
    var status: LoanStatus = LoanStatus.Pending

    //  A loan has many collections .
    // One-to-many relationship with Collection
    @OneToMany(mappedBy = "loan")
    @JsonIgnore
    var loanCollections: MutableList<LoanCollection> = mutableListOf()

    // A loan has one disbursement schedule
    // One-to-one relationship with LoanDisbursementSchedule
    @OneToOne(mappedBy = "loan")
    @JsonIgnore
    var loanDisbursementSchedule: LoanDisbursementSchedule? = null

    // Many loans belong to one client
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    lateinit var client: Client

    // Many loans belong to one client
    @ManyToOne
    @JoinColumn(name = "loan_officer_id", nullable = false)
    @JsonIgnore
    lateinit var loanOfficer: LoanOfficer

    // A loan's money circulation origin
    // One-to-one relationship with Vault
    @ManyToOne
    @JoinColumn(name = "vault_id", nullable = false)
    @JsonIgnore
    lateinit var vault: Vault

    constructor(
        loanDto: LoanDto,
        clientRepository: ClientRepository,
        loanOfficerRepository: LoanOfficerRepository,
        vaultRepository: VaultRepository
    ) : this() {
        this.amount = loanDto.amount
        this.repaymentInterval = loanDto.repaymentInterval
        this.firstRepaymentDate = loanDto.firstRepaymentDate.toDate()
        this.interestRate = loanDto.interestRate
        this.typeOfInterest = loanDto.typeOfInterest
        this.client = Crud.find {
            clientRepository.findById(loanDto.clientId)
        }
        this.loanOfficer = Crud.find {
            loanOfficerRepository.findById(loanDto.loanOfficerId)
        }
        this.vault = Crud.find {
            vaultRepository.findById(loanDto.vaultId)
        }
    }

    /**
     * Computes amount A, after nt periods, given the loan amount as the Principal P
     * @param nt number of periods, elapsed since first repayment
     * @return amount accrued after N days
     */
    private fun amountAccruedAfter(nt: Float): Float {
        return when(typeOfInterest) {
            InterestType.SIMPLE -> amount + ( amount * interestRate * nt )
            InterestType.COMPOUND -> amount + ( amount * interestRate * nt )
        }
    }

    // Expected amount to have been collected to date
    fun expectedAmountToDate(): Float {
        return amountAccruedAfter(elapsedPeriods()/repaymentDuration)
    }

    // Calculated amount
    fun calculatedAmountToDate(): Float = amountAccruedAfter(currentPeriod()/repaymentDuration)

    // Period divisor
    private fun unitDivisor(): Int = when(repaymentInterval) {
        ChronoUnit.WEEKS -> 7
        ChronoUnit.MONTHS -> 30
        ChronoUnit.YEARS -> 360
        else -> 1
    }

    /**
     * Compute number of periods elapsed after N days
     * @return completely elapsed periods: N
     */
    fun periodsAfter(days: Long): Long = days / unitDivisor()

    /**
     * Completely elapsed periods
     */
    fun elapsedPeriods(): Long = floor(currentPeriod()).toLong()

    fun elapsedDays(): Long = ChronoUnit.DAYS.between(firstRepaymentDate.toLocalDate(), Date().toLocalDate())

    fun loanPeriod(): String =  "$repaymentDuration $repaymentInterval"

    // Compute present period: Present N
    fun currentPeriod(): Float =  ((elapsedDays() + 0f) / unitDivisor())

    // Amount already collected
    fun collectedAmount(): Float = loanCollections.fold(0f) { acc, curr -> acc + curr.amount }

    /**
     * Checks if there exists a past due repayment
     * @return true if a repayment is past due, otherwise returns false.
     */
    fun defaulted(): Boolean =collectedAmount() < expectedAmountToDate()

    fun unDefaultedTotalPay(): Float = amountAccruedAfter(1f)

    fun totalPay(): Float = when(defaulted()) {
        true -> unDefaultedTotalPay()
        else -> unDefaultedTotalPay()
    }

    fun balance(): Float = totalPay() - collectedAmount()

    fun installment(): Float = unDefaultedTotalPay()/repaymentDuration

    fun installmentPattern(): String = "${installment()} per $repaymentInterval"

    // Generate the defaulted message payload
    fun defaultMessage(): String {
        return "Dear ${client.user.name}, your loan repayment for the [${currentPeriod()}] installment is past due." +
                " Please make a payment before any charges accumulate."
    }

    fun toLoanDto(): LoanDto {
        return LoanDto(
            id = id,
            status = status,
            amount = amount,
            repaymentInterval = repaymentInterval,
            repaymentDuration = repaymentDuration,
            firstRepaymentDate = firstRepaymentDate.yyyymmdd(),
            interestRate = interestRate,
            typeOfInterest = typeOfInterest,
            clientId = client.id,
            loanOfficerId = loanOfficer.id,
            vaultId = vault.id
        )
    }

    fun out() = LoanOut(
        id,
        status,
        amount,
        repaymentInterval,
        repaymentDuration,
        firstRepaymentDate.yyyymmdd(),
        interestRate,
        typeOfInterest,
        client.out(),
        loanOfficer.out(),
        vault.out()
    )

    fun toLoanInfo(): LoanInfo {
        return LoanInfo(
            loanPeriod = loanPeriod(),
            installmentPattern = installmentPattern(),
            expectedAmountToDate = expectedAmountToDate(),
            calculatedAmountToDate = calculatedAmountToDate(),
            elapsedPeriods = elapsedPeriods(),
            currentPeriod = currentPeriod(),
            elapsedDays = elapsedDays(),
            collectedAmount = collectedAmount(),
            defaulted = defaulted(),
            fine = 0f,
            balance = balance(),
            total = totalPay()
        )
    }

    fun miniStatement(): LoanMiniStatement = LoanMiniStatement(
        out(),
        toLoanInfo()
    )

    companion object {
        fun initializeMultipleLoans(
            loanDtos: List<LoanDto>,
            clientRepository: ClientRepository,
            loanOfficerRepository: LoanOfficerRepository,
            vaultRepository: VaultRepository
        ) :List<Loan> {

            val loans = loanDtos.mapIndexed { idx, loanDto ->
                try {
                    Loan(loanDto, clientRepository, loanOfficerRepository, vaultRepository)
                } catch (ex: EntryVenturesException) {
                    mapOf("loan#${idx+1}" to ex.message)
                }
            }

            loans.filter { it !is Loan }.also { errors ->
                if(errors.isNotEmpty()) {
                    throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY, errorDescription = { errors }) {
                        "Validation errors"
                    }
                }
            }

            return loans.map { it as Loan }
        }
    }
}