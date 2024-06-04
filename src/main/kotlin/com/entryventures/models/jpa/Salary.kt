package com.entryventures.models.jpa

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.io.Serializable
import java.util.Date

@Entity
@Table(name = "salaries")
class Salary: Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String

    // Date of salary creation [Disbursement date]
    @JsonProperty("created_at")
    var createdAt: Date = Date()

    @JsonProperty("amount")
    var amount: Float = 0f;

    @ManyToOne
    @JoinColumn(name = "vault_id", nullable = false)
    @JsonIgnore
    lateinit var vault: Vault

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    lateinit var user: User
}