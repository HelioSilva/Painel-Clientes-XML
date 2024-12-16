package com.computek.painel.domain.Entities

import org.springframework.data.annotation.Id

data class Arquivo (
    val ano: Int,
    val mes: String,
    var enviado: Boolean,
    var emailEnviado: String,
    val link: String
)