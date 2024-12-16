package com.computek.painel.application.DTOs

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

class EnvioArquivoFiscalDTO (
    val ano: Int,
    @field:NotBlank(message = "Campo 'Mês' é obrigatório.")
    val mes: String
)