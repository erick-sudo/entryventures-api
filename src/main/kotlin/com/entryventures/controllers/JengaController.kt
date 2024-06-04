package com.entryventures.controllers

import com.entryventures.apis.equity.JengaKeyService
import com.entryventures.apis.equity.JengaTransactions
import com.entryventures.services.ControllerService
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jenga")
class JengaController(
    private val controllerService: ControllerService,
) {

//    @GetMapping("/test/token")
//    fun testToken(): ResponseEntity<*> = runBlocking {
//
//        ResponseEntity.ok(
//            controllerService.testJengaToken()
//        )
//    }
}