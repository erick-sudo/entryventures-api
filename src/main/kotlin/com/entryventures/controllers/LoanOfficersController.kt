package com.entryventures.controllers

import com.entryventures.policies.loans.LoanAccessPolicy
import com.entryventures.repository.jpa.LoanOfficerRepository
import com.entryventures.services.ControllerService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Loan Officers", description = "Loan Officers management APIs")
@RestController
class LoanOfficersController(
    private val controllerService: ControllerService
) {
    @GetMapping("/loan-officers/stats/count")
    @LoanAccessPolicy
    fun count(): ResponseEntity<*> = controllerService.response(HttpStatus.OK, controllerService.countLoanOfficers())

    @GetMapping("/loan-officers/pagination/{pageNumber}/{pageSize}")
    fun clients(@PathVariable("pageNumber") pageNumber: Int, @PathVariable("pageSize") pageSize: Int): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getLoanOfficers(pageNumber, pageSize).map { it.out() })
    }

    @GetMapping("/loan-officers/{loanOfficerID}")
    fun showLoanOfficer(@PathVariable("loanOfficerID") loanOfficerID: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showLoanOfficer(loanOfficerID))
    }

    @PostMapping("/loan-officers/elastic/search")
    fun searchLoanOfficers(@Valid @RequestBody queryPayload: Map<String, String>): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.searchLoanOfficers(queryPayload["query"] ?: ""))
    }
}