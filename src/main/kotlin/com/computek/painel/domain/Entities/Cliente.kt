package com.computek.painel.domain.Entities

import jakarta.validation.Valid
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import jakarta.validation.constraints.*

@Document(collection = "clientes")
data class Cliente(
    @Id val id: String? = null,
    @field:NotBlank(message = "O campo 'Razão' é obrigatório e não pode estar vazio.")
    val razao: String,
    @field:NotBlank(message = "O campo 'Fantasia' é obrigatório e não pode estar vazio.")
    val fantasia: String,
    @field:NotBlank(message = "O campo 'CNPJ' é obrigatório e não pode estar vazio.")
    val cnpj: String,
    val telefone: String,
    val email: String,
    val software: String,
    @field:Valid
    val contador: Contador?,
    val tiposArquivo: TipoArquivo?,
    @field:Valid
    var arquivos:  MutableList<Arquivo>? = null
)
