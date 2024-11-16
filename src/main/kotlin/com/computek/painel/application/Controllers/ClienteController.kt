package com.computek.painel.application.Controllers

import com.computek.painel.domain.Services.ClienteService
import com.computek.painel.domain.Entities.Arquivo
import com.computek.painel.domain.Entities.Cliente
import com.computek.painel.domain.Entities.Contador
import com.computek.painel.domain.Entities.TipoArquivo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class ClienteController(private val clienteService: ClienteService) {
//    @GetMapping("")
//    // apenas um exemplo CREATE
//    fun cadastroCliente():String {
//        val novoCliente = Cliente(
//            cnpj = "00123456000133",
//            email = "suporte.computek@gmail.com",
//            razao = "Prologica",
//            fantasia = "Prologica",
//            software = "SysPDV",
//            telefone = "082 32218567",
//            contador = Contador(
//                email = "contabilidate@gmail.com",
//                nome = "Contador 2",
//                telefone = "082 0000 0000"
//            ),
//            tiposArquivo = listOf(
//                TipoArquivo(
//                    Nome = "XML"
//                )
//            ),
//            arquivos = listOf(
//                Arquivo(
//                    ano = 2024,
//                    mes = "JAN",
//                    link = "",
//                    enviado = false,
//                    emailEnviado = ""
//                )
//            )
//        );
//        val clienteSalvo = clienteService.salvar(novoCliente);
//        return "cliente Salvo"
//    }


    @GetMapping("/clientes")
    fun getClients():  List<Cliente>{
        return clienteService.listarTodos();

    }

    @GetMapping("/cliente/{cnpj}")
    fun getClienteCNPJ(@PathVariable("cnpj") cnpj: String): ResponseEntity<Cliente> {
        val cliente = clienteService.listarCNPJ(cnpj)
        return if (cliente != null) {
            ResponseEntity.ok(cliente)
        }else{
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/cliente")
    fun cadastrarCliente(@RequestBody novoCliente: Cliente): ResponseEntity<Cliente> {
        val clienteSalvo = clienteService.salvar(novoCliente)
        return ResponseEntity(clienteSalvo, HttpStatus.CREATED)
    }

}
