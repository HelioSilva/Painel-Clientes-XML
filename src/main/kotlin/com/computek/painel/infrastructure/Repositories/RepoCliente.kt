package com.computek.painel.infrastructure.Repositories

import com.computek.painel.domain.Entities.Cliente
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ClienteRepository : MongoRepository<Cliente, String> {
    // Aqui, você pode adicionar métodos de consulta personalizados, se necessário.
    fun findByCnpj(cnpj: String): Cliente?
}
