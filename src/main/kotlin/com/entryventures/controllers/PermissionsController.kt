package com.entryventures.controllers

import com.entryventures.policies.loans.LoanOpeningPolicy
import com.entryventures.repository.jpa.GroupRepository
import com.entryventures.repository.jpa.RoleRepository
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Resource Permissions", description = "Role Based Identity Access Management APIs")
@RestController
@RequestMapping("/permissions")
class PermissionsController(
    private val roleRepository: RoleRepository,
    private val groupRepository: GroupRepository
) {

    @GetMapping("/policies/{loanId}")
    @LoanOpeningPolicy
    fun testLoanPolicy(@PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return ResponseEntity.ok(
            mapOf("test" to "Loan policies")
        )
    }
}