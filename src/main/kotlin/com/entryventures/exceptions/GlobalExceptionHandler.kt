package com.entryventures.exceptions;

import com.entryventures.services.ControllerService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler(
    val controllerService: ControllerService
) {

    @ExceptionHandler(ThirdPartyApiException::class)
    fun handleThirdPartyApiException(ex: ThirdPartyApiException): ResponseEntity<*> {
        return controllerService.response(
            HttpStatus.INTERNAL_SERVER_ERROR,
            EntryVenturesExceptionResponse(
                errorCode = "500.500.1",
                errorMessage = ex.errorMessage,
                errorDescription = ex.exceptionDescription()
            )
        )
    }

    @ExceptionHandler(EntryVenturesPolicyException::class)
    fun handleEntryVenturesPoliciesException(ex: EntryVenturesPolicyException): ResponseEntity<*> {
        return controllerService.response(ex.serverStatus, ex.policyViolations())
    }

    @ExceptionHandler(EntryVenturesException::class)
    fun handleEntryVenturesException(ex: EntryVenturesException): ResponseEntity<*>? {
        val errorDescription = mutableMapOf<String, Any>()
        ex.description?.let {
            errorDescription["description"] = ex.description!!
        }
        errorDescription["error"] = ex.message
        return controllerService.response(
            ex.serverStatus,
            EntryVenturesExceptionResponse(
                errorCode = when(ex.serverStatus.value()) {
                    in 400 ..< 500 -> "400.${ex.serverStatus.value()}.0"
                    else -> "500.${ex.serverStatus.value()}.0"
                },
                errorMessage = ex.message,
                errorDescription = ex.description
            )
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException?): ResponseEntity<*>? {
        return controllerService.response(
            HttpStatus.FORBIDDEN,
            EntryVenturesExceptionResponse(
                errorCode = "403.400.0",
                errorMessage = "Forbidden Access",
                errorDescription = "Please contact your administrator"
            )
        )
    }

    // Handle DuplicateKeyException
    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(ex: DuplicateKeyException?): ResponseEntity<*>? {
        return controllerService.response(
            HttpStatus.CONFLICT,
            EntryVenturesExceptionResponse(
                errorCode = "409.400.0",
                errorMessage = "A record already exists bearing the supplied information.",
                errorDescription = ex?.message
            )
        )
    }

    // Handle HttpMessageConversionException
    @ExceptionHandler(HttpMessageConversionException::class)
    fun handleHttpMessageConversionException(ex: HttpMessageConversionException): ResponseEntity<*>? {
        return controllerService.response(
            HttpStatus.UNPROCESSABLE_ENTITY,
            EntryVenturesExceptionResponse(
                errorCode = "422.400.0",
                errorMessage = "Invalid Request Body",
                errorDescription = ex.message
            )
        )
    }

    // Handle Constraint Violation Errors
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptions(ex: ConstraintViolationException): ResponseEntity<*>? {

        return controllerService.response(
            HttpStatus.UNPROCESSABLE_ENTITY,
            EntryVenturesExceptionResponse(
                errorCode = "422.400.0",
                errorMessage = "Constraint Violations",
                errorDescription = ex.constraintViolations
            )
        )
    }

    // Handle data integrity violation exceptions
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationExceptions(ex: DataIntegrityViolationException): ResponseEntity<*> {
        return controllerService.response(
            HttpStatus.CONFLICT,
            EntryVenturesExceptionResponse(
                errorCode = "409.400.0",
                errorMessage = "Data integrity violation occurred",
                errorDescription = ex.message
            )
        )
    }

    // Field constraint validation errors
    // Handle MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<*>? {
        val errorsWrapper: MutableMap<String, Any> = HashMap()
        val errors: MutableMap<String, String?> = HashMap()
        val bindingResult = ex.bindingResult
        if (bindingResult.hasErrors()) {
            val fieldErrorList = bindingResult.fieldErrors
            for (fieldError in fieldErrorList) {
                errors[fieldError.field] = fieldError.defaultMessage
            }
        }
        errorsWrapper["errors"] = errors
        return controllerService.response(
            HttpStatus.UNPROCESSABLE_ENTITY,
            EntryVenturesExceptionResponse(
                errorCode = "422.400.0",
                errorMessage = "Field validation errors",
                errorDescription = errorsWrapper
            )
        )
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<*>? {

        return controllerService.response(
            HttpStatus.UNAUTHORIZED,
            EntryVenturesExceptionResponse(
                errorCode = "401.400.0",
                errorMessage = "Failed authentication",
                errorDescription = e.message
            )
        )
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun methodNotAllowed(ex: HttpRequestMethodNotSupportedException, req: HttpServletRequest): ResponseEntity<*>? {
        return controllerService.response(
            HttpStatus.METHOD_NOT_ALLOWED,
            EntryVenturesExceptionResponse(
                errorCode = "405.400.0",
                errorMessage = "Method not allowed",
                errorDescription = String.format("%s on  %s", ex.message, req.servletPath)
            )
        )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<*> {
        return controllerService.response(
            HttpStatus.NOT_FOUND,
            EntryVenturesExceptionResponse(
                errorCode = "405.400.0",
                errorMessage = "Resource not found",
                errorDescription = e.message
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<*>? {
        return controllerService.response(
            HttpStatus.INTERNAL_SERVER_ERROR,
            EntryVenturesExceptionResponse(
                errorCode = "500.500.0",
                errorMessage = "An internal server error occurred",
                errorDescription = e.message
            )
        )
    }
}
