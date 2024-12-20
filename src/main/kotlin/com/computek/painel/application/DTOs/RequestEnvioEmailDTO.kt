package com.computek.painel.application.DTOs

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

class RequestEnvioEmailDTO (
    @field:Min(value = 1900, message = "O ano deve ser maior ou igual a 1900.")
    @field:Max(value = 2100, message = "O ano deve ser menor ou igual a 2100.")
    val ano: Int,
    @field:NotBlank(message = "Campo 'Mês' é obrigatório.")
    val mes: String
)