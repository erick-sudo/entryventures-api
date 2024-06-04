package com.entryventures.controllers

import com.entryventures.extensions.toLoanStatus
import com.entryventures.models.dto.LoanDto
import com.entryventures.policies.loans.LoanAccessPolicy
import com.entryventures.policies.loans.LoanApprovalPolicy
import com.entryventures.policies.loans.LoanOpeningPolicy
import com.entryventures.services.ControllerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Loans", description = "Loan Management APIs")
@RestController
class LoansController(
    private val controllerService: ControllerService
) {

    @GetMapping("/schedules")
    fun getSchedules(): ResponseEntity<*> = controllerService.response(HttpStatus.OK, controllerService.getSchedules())

    @Operation(
        summary = "Retrieve the count of all loans",
        description = "Get the count of all loans at all stages.",
        tags = ["loans", "get"]
    )
    @GetMapping("/loans/stats/count")
    @LoanAccessPolicy
    fun count(): ResponseEntity<*> = controllerService.response(HttpStatus.OK, controllerService.countLoans())

    @Operation(
        summary = "Tally loans",
        description = "Get the count of all loans at all stages.",
        tags = ["loans", "get"]
    )
    @GetMapping("/loans/stats/status/tally")
    @LoanAccessPolicy
    fun loanTallyByStatus(): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.loanTallyByStatus())
    }

    @Operation(
        summary = "Retrieve Loans by status",
        description = "A tally of loans per stage of processing: (Pending, Approved, Disbursed, Closed)",
        tags = [ "loans", "statuses", "pagination", "get" ]
    )
    @GetMapping("/loans/by/{loanStatus}/{pageNumber}/{pageSize}")
    @LoanAccessPolicy
    fun loansByStatus(
        @PathVariable("loanStatus") loanStatus: String,
        @PathVariable("pageNumber") pageNumber: Int,
        @PathVariable("pageSize") pageSize: Int
    ): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.loansByStatus(loanStatus.toLoanStatus(), pageNumber, pageSize))
    }

    @Operation(
        summary = "Retrieve Loans by pages",
        description = "Implements pagination to retrieve loan records.",
        tags = [ "pagination", "loans", "get" ]
    )
    @GetMapping("/loans/pagination/{pageNumber}/{pageSize}")
    @LoanAccessPolicy
    fun loans(@PathVariable("pageNumber") pageNumber: Int, @PathVariable("pageSize") pageSize: Int): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getLoans(pageNumber, pageSize).map { it.out() })
    }

    @Operation(
        summary = "Retrieve a Loan by Id",
        description = "Get a Loan object by specifying its id. The response is Loan object with id, status, amount and client_id.",
        tags = [ "loans", "get" ]
    )
    @GetMapping("/loans/{loanId}")
    @LoanAccessPolicy
    fun showLoan(@PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showLoan(loanId).toLoanDto())
    }

    @GetMapping("/loans/loan-info/{loanId}")
    @LoanAccessPolicy
    fun showLoanInfo(@PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showLoan(loanId).miniStatement())
    }

    @Operation(
        summary = "Create loan",
        description = "Opens a new loan",
        tags = [ "loans", "post" ]
    )
    @PostMapping("/CS/loans")
    @LoanOpeningPolicy
    fun createLoan(@Valid @RequestBody loanDto: LoanDto): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createLoan(loanDto).toLoanDto())
    }

    @PostMapping("/CM/loans")
    @LoanOpeningPolicy
    fun createLoans(@Valid @RequestBody loanDtos: List<LoanDto>): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createLoans(loanDtos).map { it.toLoanDto() })
    }

    @Operation(
        summary = "Retrieve a Loan's collections by pages",
        description = "Retrieves loan collection of a specific loan but returns them in terms of page number and size of a page.",
        tags = [ "repayments", "get" ]
    )
    @GetMapping("/loans/{loanId}/collections/{pageNumber}/{pageSize}")
    @LoanAccessPolicy
    fun getLoanRepayments(
        @PathVariable("loanId") loanId: String,
        @PathVariable("pageNumber") pageNumber: Int,
        @PathVariable("pageSize") pageSize: Int
    ): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getLoanRepayments(
            loanId,
            pageNumber,
            pageSize
        ).map { it.toLoanCollectionDto() })
    }

    @Operation(
        summary = "Update loan parameters",
        description = "Modifies the parameters of a pre-existing loan by new incoming values.",
        tags = [ "loans", "patch" ]
    )
    @PatchMapping("/loans/{loanId}")
    @LoanAccessPolicy
    fun updateLoan(@PathVariable("loanId") loanId: String, @RequestBody payload: Map<String, String>): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.updateLoan(loanId, payload).toLoanDto())
    }

    @Operation(
        summary = "Delete a Loan by Id",
        description = "Completely remove a loan.",
        tags = [ "loans", "delete" ]
    )
    @DeleteMapping("/loans/{loanId}")
    @LoanAccessPolicy
    fun deleteLoan(@PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.NO_CONTENT, controllerService.deleteLoan(loanId))
    }

    @Operation(
        summary = "Loan approval",
        description = "Approves an initial loan application",
        tags = [ "approve", "get" ]
    )
    @GetMapping("/loans/{loanId}/status/approve")
    @LoanApprovalPolicy
    fun approveLoan(@PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.ACCEPTED, controllerService.approveLoan(loanId))
    }
}