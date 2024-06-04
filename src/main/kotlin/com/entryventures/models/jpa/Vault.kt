package com.entryventures.models.jpa

import com.entryventures.models.dto.VaultOut
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "vaults")
class Vault(
    @NotBlank
    @NotNull
    @JsonProperty("key")
    @SerializedName("key")
    @Column(nullable = false, unique = true)
    var key: String,

    @NotBlank
    @NotNull
    @Column(nullable = false, unique = true)
    @JsonProperty("name")
    @SerializedName("name")
    var name: String
): Serializable {
    @Id
    @JsonProperty("id")
    @SerializedName("id")
    var id: String = UUID.randomUUID().toString()

    @OneToMany(mappedBy = "vault")
    @JsonIgnore
    lateinit var loans: List<Loan>

    @OneToMany(mappedBy = "vault")
    @JsonIgnore
    lateinit var salaries: List<Salary>

    fun out() = VaultOut(id, name)
}