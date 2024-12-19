package com.computek.painel.domain.Entities

import jakarta.validation.constraints.NotBlank

data class Contador (
    val nome: String,
    val telefone: String,
    @field:NotBlank(message = "O campo 'email' do contador é obrigatório e não pode estar vazio.")
    val email: String
)