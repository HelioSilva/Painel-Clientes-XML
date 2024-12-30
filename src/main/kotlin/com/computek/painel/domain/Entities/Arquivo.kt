package com.computek.painel.domain.Entities

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.springframework.data.annotation.Id

data class Arquivo (
    val ano: Int,
    @field:NotBlank(message = "O campo 'mês' é obrigatório e não pode estar vazio.")
    var mes: String,
    var enviado: Boolean,
    var emailEnviado: String,
    val link: String
)