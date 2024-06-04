package com.entryventures.models.jpa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import java.io.Serializable

@Entity
@Table(name = "roles")
class Role(
    name: String,
    description: String
): GrantedAuthority, Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2",  strategy = "org.hibernate.id.UUIDGenerator" )
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String

    @Column(name = "name", unique = true, nullable = false)
    @JsonProperty("name")
    @NotBlank
    var name: String

    @JsonProperty("description")
    @NotBlank
    var description: String

    // Define the Many-to-Many relationship with Group
    @ManyToMany(mappedBy = "roles")
    private var groups: MutableList<Group> = mutableListOf()

    @ManyToMany(mappedBy = "roles")
    private var users: MutableList<User> = mutableListOf();

    init {
        this.name = name;
        this.description = description
    }

    override fun getAuthority(): String = this.name

    override fun toString(): String = this.name
}
