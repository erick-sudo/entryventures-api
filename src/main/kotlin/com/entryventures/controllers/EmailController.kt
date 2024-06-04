package com.entryventures.controllers

import com.entryventures.dtos.MailDto
import com.entryventures.services.ControllerService
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("info_bid/mail")
class EmailController (
    private val controllerService: ControllerService
) {

    @PostMapping("mail/text")
    fun sendMail(@RequestBody @Valid mailDto: MailDto): ResponseEntity<*> = runBlocking {
        controllerService.response(HttpStatus.OK, controllerService.sendMail(mailDto))
    }
}