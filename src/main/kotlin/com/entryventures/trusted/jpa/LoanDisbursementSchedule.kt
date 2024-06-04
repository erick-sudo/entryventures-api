package com.entryventures.trusted.jpa

import com.entryventures.models.dto.LoanDisbursementScheduleOut
import com.entryventures.models.jpa.Loan
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.io.Serializable
import java.util.Date

@Entity
@Table(name = "disbursement_schedules")
class LoanDisbursementSchedule: Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String

    @Column(name = "processed", nullable = false)
    var processed: Boolean = false

    @Column(name = "processed_at", nullable = true)
    var processedAt: Date? = null

    // A disbursement schedule belongs to a Loan
    @OneToOne
    @JoinColumn(name = "loan_id", nullable = false)
    lateinit var loan: Loan

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    lateinit var createdAt: Date

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    lateinit var updatedAt: Date

    @PrePersist
    protected fun onCreate() {
        createdAt = Date()
        updatedAt = Date()
    }

    @PreUpdate
    protected fun onUpdate() {
        updatedAt = Date()
    }

    fun out() = LoanDisbursementScheduleOut(
        id,
        loan.client.user.name,
        loan.amount,
        processed,
        createdAt,
        processedAt
    )
}