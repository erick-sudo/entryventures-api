package com.entryventures.models.jpa

import com.entryventures.models.dto.LoanOfficerOut
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "loan_officers")
class LoanOfficer(
    @Id
    @JsonProperty("id")
    var id: String = UUID.randomUUID().toString()
) {

    @OneToOne
    @JoinColumn(name = "user_id")
    lateinit var user: User

    // A loan officer administers many loans
    @OneToMany(mappedBy = "loanOfficer")
    @JsonIgnore
    lateinit var loans: List<Loan>

    fun out() = LoanOfficerOut(id, user.name, user.email, user.phone)
}