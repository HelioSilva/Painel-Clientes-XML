package com.computek.painel.domain.Services
import com.computek.painel.application.DTOs.ApiResponse
import com.computek.painel.domain.Entities.Arquivo
import com.computek.painel.domain.Entities.Cliente
import com.computek.painel.infrastructure.Repositories.ClienteRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ClienteService(private val clienteRepository: ClienteRepository) {

    fun <T> createApiResponse(
        status: Int,
        message: String,
        data: T? = null,
        errors: List<String>? = null
    ): ApiResponse<T> {
        return ApiResponse(
            status = status,
            message = message,
            data = data,
            errors = errors
        )
    }
    fun salvarCliente(cliente: Cliente): Cliente = clienteRepository.save(cliente)
    fun retornarTodosClientes(): ApiResponse<List<Cliente>> {
        try {
            return createApiResponse(
                status = HttpStatus.OK.value(),
                message = "Retorno de todos os clientes cadastrados",
                data= clienteRepository.findAll().toList()
            )
        } catch (ex: Exception){
            val errors = mutableListOf<String>();
            errors.add(ex.message.toString());
            return createApiResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "",
                errors= errors,
                data = null
            )
        }

    }
    fun retornarClientePorCNPJ(cnpj: String): Cliente? = clienteRepository.findByCnpj(cnpj)
    fun retornarClientePorId(id: String): Cliente? {
        return clienteRepository.findById(id).orElse(null)
    }
    fun buscarClientesSemArquivos(ano: Int, mes: String): ApiResponse<List<Cliente>> {
        try {
            return createApiResponse(
                status = HttpStatus.OK.value(),
                message = "Retorno de clientes sem arquivo gerado",
                data = clienteRepository.findClientesWithoutArquivoForMesEAno(ano, mes)
            )
        } catch (ex: Exception){
            val errors = mutableListOf<String>();
            errors.add(ex.message.toString());
            return  createApiResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "",
                errors = errors
            )
        }
    }
    fun buscarClientesComArquivos(ano: Int, mes: String): ApiResponse<List<Cliente>>{
        try {
            return createApiResponse(
                status = HttpStatus.OK.value(),
                message = "Retorno de clientes sem arquivo gerado",
                data = clienteRepository.findClientesWithArquivoForMesEAno(ano, mes)
            )
        } catch (ex: Exception){
            val errors = mutableListOf<String>();
            errors.add(ex.message.toString());
            return  createApiResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "",
                errors = errors
            )
        }
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
