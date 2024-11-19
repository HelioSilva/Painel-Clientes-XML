package com.computek.painel.application.Controllers

import com.computek.painel.domain.Services.ClienteService
import com.computek.painel.domain.Entities.Cliente
import jakarta.validation.Valid
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
    fun cadastrarCliente(@Valid @RequestBody novoCliente: Cliente): ResponseEntity<Cliente> {
        val clienteCadastrado = clienteService.listarCNPJ(novoCliente.cnpj) != null;
        if (clienteCadastrado){
            return  ResponseEntity(HttpStatus.CONFLICT);
        }

        val clienteSalvo = clienteService.salvar(novoCliente)
        return ResponseEntity(clienteSalvo, HttpStatus.CREATED)
    }

}
