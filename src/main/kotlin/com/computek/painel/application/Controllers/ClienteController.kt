package com.computek.painel.application.Controllers

import com.computek.painel.domain.Services.ClienteService
import com.computek.painel.domain.Entities.Cliente
import com.computek.painel.domain.Entities.Contador
import com.computek.painel.domain.Entities.TipoArquivo
import com.computek.painel.infrastructure.Repositories.ClienteRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class ClienteController(private val clienteService: ClienteService) {

    @GetMapping("/clientes")
    fun getClients(): ResponseEntity<List<Cliente>> {
        return ResponseEntity(clienteService.listarTodos(), HttpStatus.OK)

    }

    @GetMapping("/cliente/{cnpj}")
    fun getClientsByCNPJ(@PathVariable("cnpj") cnpj: String): ResponseEntity<Cliente> {
        val cliente = clienteService.listarCNPJ(cnpj)
        return if (cliente != null) {
            ResponseEntity(cliente,HttpStatus.OK)
        }else{
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping("/cliente")
    fun createClient(@Valid @RequestBody novoCliente: Cliente): ResponseEntity<Cliente> {
        val clienteCadastrado = clienteService.listarCNPJ(novoCliente.cnpj) != null;
        if (clienteCadastrado){
            return  ResponseEntity(HttpStatus.CONFLICT);
        }
        
        val clienteSalvo = clienteService.salvar(novoCliente)
        return ResponseEntity(clienteSalvo, HttpStatus.CREATED)
    }

    @PutMapping("/cliente/{cnpj}")
    fun updateClientByCNPJ(@PathVariable("cnpj") cnpj: String, @RequestBody cliente: Cliente):ResponseEntity<Cliente> {

        val clientExists = clienteService.clienteExiste(cnpj)

        if (clientExists == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        val updateContador = clientExists.contador.copy(
            nome = cliente.contador.nome,
            email = cliente.contador.email,
            telefone = cliente.contador.telefone
        )


        val updateTipoArquivo = cliente.tiposArquivo.map { novoTipo ->
            clientExists.tiposArquivo.find { it.Nome == novoTipo.Nome }?.copy(Nome = novoTipo.Nome) ?: novoTipo
        }

        val updateArquivo = clientExists.arquivos.map { novoArquivo ->
            novoArquivo.copy(
                ano = cliente.arquivos.firstOrNull()?.ano ?: novoArquivo.ano,
                mes = cliente.arquivos.firstOrNull()?.mes ?: novoArquivo.mes,
                link = cliente.arquivos.firstOrNull()?.link ?: novoArquivo.link,
                enviado = cliente.arquivos.firstOrNull()?.enviado ?: novoArquivo.enviado,
                emailEnviado = cliente.arquivos.firstOrNull()?.emailEnviado ?: novoArquivo.emailEnviado
            )
        }



        val updateClient = clientExists.copy(
            cnpj = cliente.cnpj,
            razao = cliente.razao,
            fantasia = cliente.fantasia,
            telefone = cliente.telefone,
            email = cliente.email,
            software = cliente.software,
            contador = updateContador,
            tiposArquivo = updateTipoArquivo,
            arquivos = updateArquivo
        )

        clienteService.salvar(updateClient)
        return ResponseEntity(updateClient, HttpStatus.OK)

    }

}
