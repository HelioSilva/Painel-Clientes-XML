package com.computek.painel.infrastructure.Repositories

import com.computek.painel.domain.Entities.Cliente
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ClienteRepository : MongoRepository<Cliente, String> {
    // Aqui, você pode adicionar métodos de consulta personalizados, se necessário.
    fun findByCnpj(cnpj: String): Cliente?

    @Query("{ 'arquivos': { '\$not': { '\$elemMatch': { 'ano': ?0, 'mes': ?1 } } } }")
    fun findClientesWithoutArquivoForMesEAno(ano: Int, mes: String): List<Cliente>

    @Query("{ 'arquivos': { '\$elemMatch': { 'ano': ?0, 'mes': ?1 } } }")
    fun findClientesWithArquivoForMesEAno(ano: Int, mes: String): List<Cliente>

}
