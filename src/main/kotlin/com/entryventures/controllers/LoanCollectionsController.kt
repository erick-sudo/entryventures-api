package com.entryventures.controllers

import com.entryventures.models.dto.CollectionTrigger
import com.entryventures.models.dto.LoanCollectionDto
import com.entryventures.services.ControllerService
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

@Tag(name = "Loan Collections", description = "Loan Collection APIs")
@RestController
class LoanCollectionsController(
    private val controllerService: ControllerService
) {

    @GetMapping("/loans/collections/{loanId}/count")
    fun getLoanCollectionsCountByLoanId(@PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.countLoanCollectionsByLoanId(loanId))
    }

    @GetMapping("/loan/collections")
    fun loanCollections(): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getLoanCollections().map { it.toLoanCollectionDto() })
    }

    @GetMapping("/loan/collections/{loanCollectionId}")
    fun showLoanCollection(@PathVariable("loanCollectionId") loanCollectionId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showLoanCollection(loanCollectionId).toLoanCollectionDto())
    }

//    @PostMapping("/trigger/loan/collections")
//    fun triggerLoanCollection(@Valid @RequestBody trigger: CollectionTrigger): ResponseEntity<*> = runBlocking {
//        controllerService.response(HttpStatus.OK, controllerService.triggerCollection(trigger))
//    }

    @PostMapping("/loan/collections")
    fun createLoanCollection(@Valid @RequestBody loanCollectionDto: LoanCollectionDto): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createLoanCollection(loanCollectionDto).toLoanCollectionDto())
    }

    @PatchMapping("/loan/collections/{loanCollectionId}")
    fun updateLoanCollection(@PathVariable("loanCollectionId") loanCollectionId: String, @RequestBody payload: Map<String, String>,): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.updateLoanCollection(loanCollectionId, payload).toLoanCollectionDto())
    }

    @DeleteMapping("/loan/collections/{loanCollectionId}")
    fun deleteLoanCollection(@PathVariable("loanCollectionId") loanCollectionId: String, @RequestBody payload: Map<String, String>,): ResponseEntity<*> {
        return controllerService.response(HttpStatus.NO_CONTENT, controllerService.deleteLoanCollection(loanCollectionId))
    }
}