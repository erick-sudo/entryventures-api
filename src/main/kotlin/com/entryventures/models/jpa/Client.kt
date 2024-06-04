package com.entryventures.models.jpa

import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.models.dto.ClientOut
import com.entryventures.repository.jpa.ClientRepository
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "clients")
class Client : Serializable {
    @Id
    @JsonProperty("id")
    var id: String = UUID.randomUUID().toString()

    @Column(name = "address", nullable = false)
    @JsonProperty("address")
    @NotBlank
    lateinit var address: String

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    // A client has many loans
    @OneToMany(mappedBy = "client")
    @JsonIgnore
    lateinit var loans: List<Loan>

    fun out() = ClientOut(id, user.name, user.email, user.phone)

    companion object {
        fun initializeClients(
            clients: List<Client>,
            clientRepository: ClientRepository
        ): List<Client> {

//            clients.filter { clientRepository.existsByEmailOrPhone(it.user.email, it.user.phone) }.also { existingClients ->
//                if(existingClients.isNotEmpty()) {
//                    throw EntryVenturesException(HttpStatus.CONFLICT, errorDescription = { existingClients.map { mapOf("email" to it.user.email, "phone" to it.user.phone) } }) {
//                        "Email or phone numbers already taken"
//                    }
//                }
//            }

            return emptyList()
        }
    }
}