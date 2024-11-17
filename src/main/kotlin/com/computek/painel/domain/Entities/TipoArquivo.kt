package com.computek.painel.domain.Entities

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class TipoArquivo @JsonCreator constructor(
    @JsonProperty("Nome") val Nome: String
)