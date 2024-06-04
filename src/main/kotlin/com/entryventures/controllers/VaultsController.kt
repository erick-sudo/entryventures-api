package com.entryventures.controllers

import com.entryventures.models.jpa.Vault
import com.entryventures.services.ControllerService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Vaults [Money Centers]", description = "Financial Institutions management APIs")
@RestController
class VaultsController(
    private val controllerService: ControllerService
) {

    @GetMapping("/vaults")
    fun vaults(): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getVaults())
    }

    @GetMapping("/vaults/{vaultId}")
    fun showVault(@PathVariable("vaultId") vaultId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showVault(vaultId))
    }

    @PostMapping("/vaults/elastic/search")
    fun searchVaults(@Valid @RequestBody queryPayload: Map<String, String>): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.searchVaults(queryPayload["query"] ?: ""))
    }

    @PostMapping("/CS/vaults")
    fun createVault(@Valid @RequestBody vault: Vault): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createVault(vault))
    }

    @PostMapping("/CM/vaults")
    fun createVaults(@Valid @RequestBody vaults: List<Vault>): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createVaults(vaults))
    }

    @PatchMapping("/vaults/{vaultId}")
    fun updateVault(@RequestBody payload: Map<String, String>, @PathVariable("vaultId") vaultId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.updateVault(vaultId, payload))
    }

    @DeleteMapping("/vaults/{vaultId}")
    fun deleteVault(@PathVariable("vaultId") vaultId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.NO_CONTENT, controllerService.deleteVault(vaultId))
    }
}