package com.computek.painel.application.DTOs

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

class RequestUploadDTO (
  @field:NotNull(message = "O arquivo é obrigatório.")
  val file:MultipartFile,
  @field:NotBlank(message = "Campo 'Mês' é obrigatório.")
  val mes: String,
  @field:Min(value = 1900, message = "O ano deve ser maior ou igual a 1900.")
  @field:Max(value = 2100, message = "O ano deve ser menor ou igual a 2100.")
  val ano: Int
)