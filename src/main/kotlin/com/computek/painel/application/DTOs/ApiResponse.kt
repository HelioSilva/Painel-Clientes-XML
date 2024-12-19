package com.computek.painel.application.DTOs

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T? = null,
    val errors: List<String>? = null
)