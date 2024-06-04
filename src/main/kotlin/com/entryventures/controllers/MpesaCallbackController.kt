package com.entryventures.controllers

import com.entryventures.apis.mpesa.express.AsyncExpressBody
import com.entryventures.trusted.mpesa.B2CResult
import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.models.dto.CollectionTrigger
import com.entryventures.services.ControllerService
import com.entryventures.services.Crud
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Mpesa Callbacks", description = "Mpesa Transactions APIs")
@RestController
@RequestMapping("/entry-ventures/mpesa/callback")
class MpesaCallbackController(
    private val controllerService: ControllerService
) {

    @PostMapping("/loan-repayment/stk-callback")
    fun mpesaExpressCallback(@RequestBody expressBody: AsyncExpressBody): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.mpesaExpressCallback(expressBody))
    }

    @PostMapping("/loan-repayment/request-stk")
    fun triggerLoanCollection(@Valid @RequestBody trigger: CollectionTrigger): ResponseEntity<*> = runBlocking {
        ResponseEntity.ok(controllerService.triggerLoanCollection(trigger))
    }

    @PostMapping("/b2c-callback")
    fun b2cCallback(@RequestBody b2cResult: B2CResult): ResponseEntity<*> = runBlocking {
        controllerService.sendB2CCallBackResultToQueue(b2cResult)
        controllerService.response(HttpStatus.OK, mapOf("message" to "Success"))
    }
}