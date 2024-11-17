package com.computek.painel.domain.Entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "clientes")
data class Cliente(
    @Id val id: String? = null,
    val razao: String,
    val fantasia: String,
    val cnpj: String,
    val telefone: String,
    val email: String,
    val software: String,
    val contador: Contador,
    val tiposArquivo: List<TipoArquivo>,
    val arquivos: List<Arquivo>
)
