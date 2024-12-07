package com.computek.painel.domain.Entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import jakarta.validation.constraints.*

@Document(collection = "clientes")
data class Cliente(
    @Id val id: String? = null,
    @field:NotBlank(message = "O campo 'Razão' é obrigatório e não pode estar vazio.")
    val razao: String,
    val fantasia: String,
    @field:NotBlank(message = "O campo 'CNPJ' é obrigatório e não pode estar vazio.")
    val cnpj: String,
    val telefone: String,
    val email: String,
    val software: String,
    val contador: Contador?,
    val tiposArquivo: TipoArquivo?,
    val arquivos: List<Arquivo>?
)
