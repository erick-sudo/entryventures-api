package com.entryventures.models.jpa

import com.entryventures.models.EmploymentPosition
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.io.Serializable
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "employees")
class Employee: Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String

    // Employee's position
    var position: EmploymentPosition = EmploymentPosition.LoanOfficer

    // Pay
    var salary: Float = 0f

    // Pay rate
    var period: ChronoUnit = ChronoUnit.MONTHS

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    lateinit var user: User
}