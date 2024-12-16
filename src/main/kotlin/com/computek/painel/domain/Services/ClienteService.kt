package com.computek.painel.domain.Services
import com.computek.painel.domain.Entities.Arquivo
import com.computek.painel.domain.Entities.Cliente
import com.computek.painel.infrastructure.Repositories.ClienteRepository
import org.springframework.stereotype.Service

@Service
class ClienteService(private val clienteRepository: ClienteRepository) {
    fun salvarCliente(cliente: Cliente): Cliente = clienteRepository.save(cliente)
    fun retornarTodosClientes(): List<Cliente> = clienteRepository.findAll().toList()
    fun retornarClientePorCNPJ(cnpj: String): Cliente? = clienteRepository.findByCnpj(cnpj)
    fun retornarClientePorId(id: String): Cliente? {
        return clienteRepository.findById(id).orElse(null)
    }
    fun buscarClientesSemArquivos(ano: Int, mes: String): List<Cliente> {
        return clienteRepository.findClientesWithoutArquivoForMesEAno(ano, mes)
    }
    fun buscarClientesComArquivos(ano: Int, mes: String): List<Cliente> {
        return clienteRepository.findClientesWithArquivoForMesEAno(ano, mes)
    }
    fun buscarArquivoEspecifico(cliente: Cliente, ano: Int, mes: String): Arquivo? {
        return cliente.arquivos?.find { it.ano == ano && it.mes.equals(mes, ignoreCase = true) }
    }

    fun atualizarStatusEnviado(cliente: Cliente, ano: Int, mes: String, emailEnviado: String): Boolean {
        try {
            cliente.arquivos?.forEach { arquivo ->
                if (arquivo.ano == ano && arquivo.mes.equals(mes, ignoreCase = true)) {
                    arquivo.enviado = true;
                    arquivo.emailEnviado = emailEnviado;
                }
            }
            clienteRepository.save(cliente);
            return true
        } catch (ex: Exception){
            return false;
        }

    }

}
