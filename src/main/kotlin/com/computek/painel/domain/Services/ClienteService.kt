package com.computek.painel.domain.Services
import com.computek.painel.application.DTOs.ApiResponse
import com.computek.painel.application.DTOs.RequestEnvioEmailDTO
import com.computek.painel.application.DTOs.ResponseUploadDTO
import com.computek.painel.domain.Entities.Arquivo
import com.computek.painel.domain.Entities.Cliente
import com.computek.painel.infrastructure.Repositories.ClienteRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class ClienteService(private val clienteRepository: ClienteRepository,
                     private val emailService: EmailService) {

    @Value("\${file.upload-dir}")
    private lateinit var uploadDir: String;

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
    fun salvarCliente(cliente: Cliente): ApiResponse<Cliente> {

        if (cliente.id == null){
            val ConsultaCliente = consultarCliente(cliente.cnpj);

            if (ConsultaCliente != null){
                return createApiResponse(
                    status = HttpStatus.CONFLICT.value(),
                    message = "Cliente já cadastrado.",
                    data = null
                )
            }
        }

        try {
            val savedCliente = clienteRepository.save(cliente);
            return createApiResponse(
                status = HttpStatus.CREATED.value(),
                message = "Cliente salvo com sucesso!",
                data = savedCliente
            )
        } catch (ex: Exception){
            return createApiResponse(
                status = HttpStatus.FORBIDDEN.value(),
                message = "Erro no cadastro do novo cliente",
                data = null
            )
        }
    }

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

    fun consultarCliente(cnpj: String): Cliente? {
        return clienteRepository.findByCnpj(cnpj)
    }
    fun retornarClientePorCNPJ(cnpj: String): ApiResponse<Cliente> {
        if (cnpj == ""){
            return createApiResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "Cnpj não informado",
                data = null
            )
        }
        val cliente = consultarCliente(cnpj);
        if (cliente == null){
            return createApiResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = "Nenhum cliente encontrado",
                data = null,
            )
        }

        return createApiResponse(
            status = HttpStatus.OK.value(),
            message = "",
            data = cliente,
        )
    }
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

    fun RetornaNomeArquivo(cnpj:String, mes: String, ano: Int):String {
        return  cnpj+'_'+mes+'_'+ano.toString()
    }

    fun RetornaExtensaoArquivo(file: MultipartFile): String {
        return file.originalFilename?.substringAfterLast('.', "") ?: ""
    }

    fun ValidaExistenciaDiretorio(){
        val directory = File(uploadDir)
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    fun SalvarArquivoDiretorio(file: MultipartFile, nomeArquivo: String, extensao: String){
        // Salvar arquivo
        val filePath = Paths.get( uploadDir, nomeArquivo +'.'+ extensao)
        file.inputStream.use { inputStream ->
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    fun validarArquivosUnicos(mes: String, ano:Int, arquivos: List<Arquivo>?) {
        val duplicado = arquivos?.find { it.mes == mes && it.ano == ano  }
        if (duplicado != null) {
            throw IllegalArgumentException("Não é permitido ter arquivos duplicados com o mesmo ano e mês.")
        }

    }


    fun uploadArquivoCliente(
        id: String,
        file: MultipartFile,
        mes: String,
        ano: Int,
        request: HttpServletRequest
    ): ApiResponse<ResponseUploadDTO>{

        if (file.isEmpty) {
            return createApiResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "O campo arquivo é obrigatório para realizar o upload!"
            )
        }

        val respostaConsultaCliente = retornarClientePorId(id);

        if (respostaConsultaCliente == null) {
            return createApiResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = "Cliente não encontrado"
            )
        }

        try {
            validarArquivosUnicos(mes, ano,  respostaConsultaCliente.arquivos);
            ValidaExistenciaDiretorio();

            val nomeArquivo = RetornaNomeArquivo(respostaConsultaCliente.cnpj, mes,ano);
            val extension = RetornaExtensaoArquivo(file);

            SalvarArquivoDiretorio(file, nomeArquivo, extension);

            // Gerar link para download
            val downloadUrl = "${request.scheme}://${request.serverName}:${request.serverPort}/v1/cliente/download/${nomeArquivo+'.'+extension}"

            if (respostaConsultaCliente.arquivos == null) {
                respostaConsultaCliente.arquivos = mutableListOf() // Inicializa a lista, se for nula
            }
            val arquivo: Arquivo = Arquivo(
                ano = ano,
                mes = mes,
                enviado = false,
                emailEnviado = "",
                link = downloadUrl);

            respostaConsultaCliente.arquivos?.add(arquivo);

            salvarCliente(respostaConsultaCliente);

            return createApiResponse(
                status = HttpStatus.CREATED.value(),
                message = "Arquivo enviado com sucesso!",
                data = ResponseUploadDTO(
                    url = downloadUrl
                )
            )
        } catch (ex: Exception) {
            return createApiResponse(
                status = HttpStatus.FORBIDDEN.value(),
                message = ex.message!!,
                data = null
            )
        }
    }

    fun deleteArquivoCliente(
        id: String,
        mes: String,
        ano: Int
    ): ApiResponse<ResponseUploadDTO>{

        val respostaConsultaCliente = retornarClientePorId(id);

        if (respostaConsultaCliente == null) {
            return createApiResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = "Cliente não encontrado"
            )
        }

        try {
            // Remover o arquivo correspondente
            val arquivoRemovido = respostaConsultaCliente.arquivos?.removeIf { it.ano == ano && it.mes == mes } ?: false

            if (arquivoRemovido) {
                clienteRepository.save(respostaConsultaCliente) // Salva o cliente atualizado no banco de dados
                return createApiResponse(
                    status = HttpStatus.OK.value(),
                    message = "Arquivo deletado com sucesso!"
                )
            }

            return createApiResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = "Arquivo não encontrado!"
            )

        } catch (ex: Exception) {
            return createApiResponse(
                status = HttpStatus.FORBIDDEN.value(),
                message = ex.message!!,
                data = null
            )
        }
    }

    fun EnviarArquivoNoEmail(idCliente: String, body: RequestEnvioEmailDTO):ApiResponse<Any> {

        val consultaCliente = retornarClientePorId(idCliente);
        if (consultaCliente == null){
            return createApiResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = "Cliente não encontrado"
            )
        }

        try {
            if (consultaCliente.contador?.email == ""){
                return createApiResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    message = "Contador não possui email cadastrado"
                )
            }

            val arquivoEncontrado = buscarArquivoEspecifico(consultaCliente, body.ano, body.mes);
            if (arquivoEncontrado == null){
                return createApiResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    message = "Nenhum arquivo encontrado para enviar no email"
                )
            }

            val emailEnviado = emailService.EnviarEmail(
                consultaCliente,
                arquivoEncontrado
            );

            if (emailEnviado){
                atualizarStatusEnviado(
                    consultaCliente,
                    body.ano,
                    body.mes,
                    consultaCliente.contador!!.email
                );
            }

            return createApiResponse(
                status = HttpStatus.OK.value(),
                message = "Email enviado com sucesso!"
            )
        } catch (ex: Exception){
            return createApiResponse(
                status = HttpStatus.FORBIDDEN.value(),
                message = "Falha no envio do email!"
            )
        }
    }


    fun AlternarStatusCliente(idCliente: String):ApiResponse<Any> {

        val consultaCliente = retornarClientePorId(idCliente);
        if (consultaCliente == null){
            return createApiResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = "Cliente não encontrado"
            )
        }

        try {
            consultaCliente.ativo = !consultaCliente.ativo;
            clienteRepository.save(consultaCliente);

            return createApiResponse(
                status = HttpStatus.OK.value(),
                message = "Status alterado com sucesso!"
            )
        } catch (ex: Exception){
            return createApiResponse(
                status = HttpStatus.FORBIDDEN.value(),
                message = "Status não alterado!"
            )
        }
    }

}
