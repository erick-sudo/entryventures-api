package com.entryventures.controllers

import com.entryventures.models.jpa.Client
import com.entryventures.policies.loans.LoanAccessPolicy
import com.entryventures.services.ControllerService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.math.cos

@Tag(name = "Clients", description = "Clients management APIs")
@RestController
class ClientsController(
    private val controllerService: ControllerService
) {

    @GetMapping("/clients/stats/count")
    @LoanAccessPolicy
    fun count(): ResponseEntity<*> = controllerService.response(HttpStatus.OK, controllerService.countClients())

    @GetMapping("/clients/pagination/{pageNumber}/{pageSize}")
    fun clients(@PathVariable("pageNumber") pageNumber: Int, @PathVariable("pageSize") pageSize: Int): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getClients(pageNumber, pageSize).map { it.out() })
    }

    @GetMapping("/clients/{clientId}")
    fun showClient(@PathVariable("clientId") clientId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showClient(clientId))
    }

    @PostMapping("/clients/elastic/search")
    fun searchClients(@Valid @RequestBody queryPayload: Map<String, String>): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.searchClients(queryPayload["query"] ?: ""))
    }

//    @PostMapping("/CS/clients")
//    fun createClient(@Valid @RequestBody client: Client): ResponseEntity<*> {
//        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createClient(client, "password"))
//    }

//    @PostMapping("/CM/clients")
//    fun createClients(@Valid @RequestBody clients: List<Client>): ResponseEntity<*> {
//        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createClients(clients))
//    }

//    @PatchMapping("/clients/{clientId}")
//    fun updateClient(@RequestBody payload: Map<String, String>, @PathVariable("clientId") clientId: String): ResponseEntity<*> {
//        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.updateClient(clientId, payload))
//    }

//    @DeleteMapping("/clients/{clientId}")
//    fun deleteClient(@PathVariable("clientId") clientId: String): ResponseEntity<*> {
//        return controllerService.response(httpStatus = HttpStatus.NO_CONTENT, controllerService.deleteClient(clientId))
//    }
}