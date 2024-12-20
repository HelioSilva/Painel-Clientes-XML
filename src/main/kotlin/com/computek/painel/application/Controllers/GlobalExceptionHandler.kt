package com.computek.painel.application.Controllers

import com.computek.painel.application.DTOs.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    fun <T> createApiResponse(
        status: Int,
        message: String,
        data: T? = null,
        errors: List<String>? = null
    ): ApiResponse<T> {
        return ApiResponse(
            status = status,
            message = message,
            data = data,
            errors = errors
        )
    }


    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        // Capturar os erros de validação
        val errors = ex.bindingResult.allErrors.map { error ->
            if (error is FieldError) {
                "Field '${error.field}': ${error.defaultMessage}"
            } else {
                error.defaultMessage ?: "Unknown validation error"
            }
        }

        // Criar um ApiResponse com os erros
        val errorResponse = createApiResponse<Nothing>(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Validation error",
            errors = errors
        )

        return ResponseEntity.badRequest().body(errorResponse)
    }
}
