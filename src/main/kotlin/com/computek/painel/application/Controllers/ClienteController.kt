package com.computek.painel.application.Controllers

import com.computek.painel.Config.MapperConfig
import com.computek.painel.domain.Services.ClienteService
import com.computek.painel.domain.Entities.Cliente
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
class ClienteController(private val clienteService: ClienteService,
                        private val modelMapper: MapperConfig) {

    fun ConsultaCliente(cnpj: String): Cliente? {
        return clienteService.listarCNPJ(cnpj);
    }

    @GetMapping("/clientes")
    fun getClients(): ResponseEntity<List<Cliente>> {
        return ResponseEntity(clienteService.listarTodos(), HttpStatus.OK)
    }

    @GetMapping("/cliente/{cnpj}")
    fun getClientsByCNPJ(@PathVariable("cnpj") cnpj: String): ResponseEntity<Cliente> {
        val cliente = clienteService.listarCNPJ(cnpj);
        if (cliente != null) {
           return ResponseEntity(cliente,HttpStatus.OK)
        }

        return  ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/cliente")
    fun createClient(@Valid @RequestBody novoCliente: Cliente): ResponseEntity<Cliente> {
        if (ConsultaCliente(novoCliente.cnpj) != null){
            return  ResponseEntity(HttpStatus.CONFLICT);
        }
        
        val clienteSalvo = clienteService.salvar(novoCliente)
        return ResponseEntity(clienteSalvo, HttpStatus.CREATED)
    }

    @PutMapping("/cliente/{cnpj}")
    fun updateClientByCNPJ(@PathVariable("cnpj") cnpj: String, @RequestBody body: Cliente):ResponseEntity<Cliente> {
        val clienteConsultado =  ConsultaCliente(cnpj)
        if (clienteConsultado == null){
            return  ResponseEntity(HttpStatus.NOT_FOUND);
        }

        val updatedCliente = clienteService.salvar(body)
        return ResponseEntity(updatedCliente, HttpStatus.OK)

    }

}
