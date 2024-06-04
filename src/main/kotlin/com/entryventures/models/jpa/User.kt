package com.entryventures.models.jpa

import com.entryventures.apis.mpesa.RegisteredMpesaClient
import com.entryventures.models.dto.UserDto
import com.entryventures.security.PasswordService
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.springframework.security.core.GrantedAuthority
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "mpesa_users")
class User() : RegisteredMpesaClient, Serializable, Authenticatable {

    @Id
    var id: String = UUID.randomUUID().toString()

    @Column(nullable = false)
    @NotNull
    @JsonProperty("name")
    lateinit var name: String

    @Column(nullable = false)
    @NotNull
    @JsonProperty("user_name")
    lateinit var userName: String

    @Column(nullable = false)
    @NotNull
    @JsonProperty("email")
    override lateinit var email: String

    @Column(nullable = false)
    @NotNull
    @JsonProperty("phone")
    override lateinit var phone: String

    // A loan officer managing a specific set of loans
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    lateinit var salaries: List<Salary>

    @Column(nullable = false)
    @JsonIgnore
    override lateinit var passwordDigest: String

    // Many-to-Many relationship with Role
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    private var roles: MutableList<Role> = mutableListOf();

    // Many-to-Many relationship with Group
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_groups",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "group_id")]
    )
    private var groups: MutableList<Group> = mutableListOf()

    /**
     * Secondary constructor
     * @param userName User's username
     * @param email User's email address
     * @param phone User's phone number
     */
    constructor(
        name: String,
        userName: String,
        email: String,
        phone: String
    ) : this() {
        this.name = name
        this.userName = userName
        this.email = email
        this.phone = phone
    }

    fun getRoles() = roles
    fun getGroups() = groups

    fun userDto() = UserDto(this)

    /**
     * Get users authorities
     * Concatenates the roles and groups into a unique set
     */
    override fun authorities(): MutableList<GrantedAuthority> = (roles + groups).toMutableList()

    override val authorities: List<String>
        get() = authorities().map { it.authority }

    fun addRole(role: Role) {
        roles.add(role);
    }

    fun addGroup(group: Group) {
        groups.add(group);
    }

    override fun authenticate(password: String): Boolean {
        return PasswordService.verifyPassword(password, this.passwordDigest);
    }
}