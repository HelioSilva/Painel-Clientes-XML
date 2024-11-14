package com.computek.painel.domain.Services
import com.computek.painel.domain.Entities.Cliente
import com.computek.painel.infrastructure.Repositories.ClienteRepository
import org.springframework.stereotype.Service

@Service
class ClienteService(private val clienteRepository: ClienteRepository) {
    fun salvar(cliente: Cliente): Cliente = clienteRepository.save(cliente)
}