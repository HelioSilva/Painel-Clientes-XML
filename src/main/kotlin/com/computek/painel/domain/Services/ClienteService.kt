package com.computek.painel.domain.Services
import com.computek.painel.domain.Entities.Cliente
import com.computek.painel.infrastructure.Repositories.ClienteRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.streams.toList

@Service
class ClienteService(private val clienteRepository: ClienteRepository) {
    fun salvar(cliente: Cliente): Cliente = clienteRepository.save(cliente)
    fun listarTodos(): List<Cliente> = clienteRepository.findAll().toList()
    fun listarCNPJ(cnpj: String): Cliente? = clienteRepository.findByCnpj(cnpj)
}
