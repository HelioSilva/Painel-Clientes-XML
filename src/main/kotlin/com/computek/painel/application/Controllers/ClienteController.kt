package com.computek.painel.application.Controllers

import com.computek.painel.domain.Services.ClienteService
import com.computek.painel.domain.Entities.Arquivo
import com.computek.painel.domain.Entities.Cliente
import com.computek.painel.domain.Entities.Contador
import com.computek.painel.domain.Entities.TipoArquivo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class ClienteController(private val clienteService: ClienteService) {
    @GetMapping("")
    // apenas um exemplo CREATE
    fun getPrinters():String{
        val novoCliente = Cliente(
            cnpj = "00657034000153",
            email = "suporte.computek@gmail.com",
            razao = "Computek",
            fantasia = "Computek",
            software = "SysPDV",
            telefone = "082 32218567",
            contador = Contador(
                email = "contabilidate@gmail.com",
                nome = "Contador 1",
                telefone = "082 0000 0000"
            ),
            tiposArquivo = listOf(
                TipoArquivo(
                    Nome = "XML"
                )
            ),
            arquivos = listOf(
                Arquivo(
                    ano = 2024,
                    mes = "JAN",
                    link = "",
                    enviado = false,
                    emailEnviado = ""
                )
            )
        );
        val clienteSalvo = clienteService.salvar(novoCliente);
        return "ResponseEntity(clienteSalvo, HttpStatus.CREATED)"
    }
}