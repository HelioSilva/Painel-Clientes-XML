package com.computek.painel.domain.Entities

data class Arquivo (
    val ano: Int,
    val mes: String,
    val enviado: Boolean,
    val emailEnviado: String,
    val link: String
)