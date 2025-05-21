package org.example.mdblog.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalErrorHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusE(e: ResponseStatusException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(e.statusCode.value(), e.reason ?: "Unknown error"),
            e.statusCode
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericE(): ResponseEntity<ErrorResponse> = ResponseEntity(
        INTERNAL_ERROR, INTERNAL_ERROR_STATUS
    )

    companion object {
        private val INTERNAL_ERROR_STATUS = HttpStatus.INTERNAL_SERVER_ERROR
        private val INTERNAL_ERROR =
            ErrorResponse(INTERNAL_ERROR_STATUS.value(), "Internal server error")
    }
}

data class ErrorResponse(
    val status: Int,
    val message: String
)
