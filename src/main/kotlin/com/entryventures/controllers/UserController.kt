package com.entryventures.controllers

import com.entryventures.models.jpa.User
import com.entryventures.services.ControllerService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@Tag(name = "Users", description = "Users management APIs")
@RestController
class UserController(
    private val controllerService: ControllerService
) {

    @GetMapping("/users")
    fun users(): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getUsers())
    }

    @GetMapping("/users/{userId}")
    fun showUser(@PathVariable("userId") userId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showUser(userId))
    }

    @PostMapping("/users")
    fun createUser(@Valid @RequestBody user: User): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createUser(user))
    }

    @PatchMapping("/users/{userId}")
    fun updateUser(@RequestBody payload: Map<String, String>, @PathVariable("userId") userId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.updateUser(userId, payload))
    }

    @DeleteMapping("/users/{userId}")
    fun deleteUser(@PathVariable("userId") userId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.NO_CONTENT, controllerService.deleteUser(userId))
    }
}