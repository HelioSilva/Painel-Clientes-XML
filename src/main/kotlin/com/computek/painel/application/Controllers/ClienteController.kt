package com.computek.painel.application.Controllers

import com.computek.painel.application.DTOs.ApiResponse
import com.computek.painel.application.DTOs.EnvioArquivoFiscalDTO
import com.computek.painel.domain.Entities.Arquivo
import com.computek.painel.domain.Services.ClienteService
import com.computek.painel.domain.Entities.Cliente
import com.computek.painel.domain.Services.EmailService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
class ClienteController(private val clienteService: ClienteService,
                        private val emailService: EmailService
) {

    @Value("\${file.upload-dir}")
    private lateinit var uploadDir: String


    @GetMapping("/clientes")
    fun getClients(): ResponseEntity<ApiResponse<List<Cliente>>> {
        val retorno = clienteService.retornarTodosClientes();
        return ResponseEntity.status(retorno.status).body(retorno)
    }

    @GetMapping("/clientes/{gerado}")
    fun buscarClientesSemArquivos(
        @PathVariable("gerado") gerado: Boolean,
        @RequestParam ano: Int,
        @RequestParam mes: String
    ): ResponseEntity<ApiResponse<List<Cliente>>> {
        var retorno: ApiResponse<List<Cliente>>;
        if (gerado) {
            retorno = clienteService.buscarClientesComArquivos(ano, mes)
        }else {
            retorno = clienteService.buscarClientesSemArquivos(ano, mes)
        }

        return ResponseEntity.status(retorno.status).body(retorno)
    }

    @GetMapping("/cliente/{cnpj}")
    fun getClientsByCNPJ(@PathVariable("cnpj") cnpj: String): ResponseEntity<Cliente> {
        val cliente = clienteService.retornarClientePorCNPJ(cnpj);
        if (cliente != null) {
           return ResponseEntity(cliente,HttpStatus.OK)
        }
        return  ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/cliente")
    fun createClient(@Valid @RequestBody novoCliente: Cliente): ResponseEntity<Cliente> {
        if (clienteService.retornarClientePorCNPJ(novoCliente.cnpj) != null){
            return  ResponseEntity(HttpStatus.CONFLICT);
        }
        val clienteSalvo = clienteService.salvarCliente(novoCliente)
        return ResponseEntity(clienteSalvo, HttpStatus.CREATED)
    }

    @PutMapping("/cliente/{cnpj}")
    fun updateClientByCNPJ(@PathVariable("cnpj") cnpj: String,@Valid  @RequestBody body: Cliente):ResponseEntity<Cliente> {
        val clienteConsultado =  clienteService.retornarClientePorCNPJ(cnpj)
        if (clienteConsultado == null){
            return  ResponseEntity(HttpStatus.NOT_FOUND);
        }
        val updatedCliente = clienteService.salvarCliente(body)
        return ResponseEntity(updatedCliente, HttpStatus.OK)
    }

    @PostMapping("/cliente/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(@RequestParam("file") file: MultipartFile,
                   @RequestParam("cnpj") cnpj: String,
                   @RequestParam("mes") mes: String,
                   @RequestParam("ano") ano: Int,request: HttpServletRequest): ResponseEntity<Map<String, String>> {

        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(mapOf("error" to "O arquivo não pode estar vazio"))
        }

        val clienteEncontrado: Cliente? = clienteService.retornarClientePorCNPJ(cnpj);

        if (clienteEncontrado == null) {
            return ResponseEntity.badRequest().body(mapOf("error" to "O cliente não está na base de dados"))
        }

        try {
            // Criar diretório, se não existir
            val directory = File(uploadDir)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val nomeArquivo: String = cnpj+'_'+mes+'_'+ano;
            val extension = file.originalFilename?.substringAfterLast('.', "") ?: ""

            // Salvar arquivo
            val filePath = Paths.get(uploadDir, nomeArquivo+'.'+extension)
            file.inputStream.use { inputStream ->
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
            }

            // Gerar link para download
            val downloadUrl = "${request.scheme}://${request.serverName}:${request.serverPort}/v1/cliente/download/${nomeArquivo+'.'+extension}"

            if (clienteEncontrado.arquivos == null) {
                clienteEncontrado.arquivos = mutableListOf() // Inicializa a lista, se for nula
            }
            val arquivo: Arquivo = Arquivo(ano = ano, mes = mes,
                enviado = false, emailEnviado = "", link = downloadUrl);

            clienteEncontrado.arquivos?.add(arquivo);

            clienteService.salvarCliente(clienteEncontrado);

            return ResponseEntity.ok(mapOf("url" to downloadUrl))
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to "Erro ao salvar o arquivo"))
        }
    }

    @GetMapping("/cliente/download/{filename}")
    fun downloadFile(@PathVariable filename: String, response: HttpServletResponse) {
        val filePath = Paths.get(uploadDir, filename)
        if (!Files.exists(filePath)) {
            response.status = HttpServletResponse.SC_NOT_FOUND
            response.writer.write("Arquivo não encontrado")
            return
        }

        response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
        response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
        Files.copy(filePath, response.outputStream)
        response.outputStream.flush()
    }

    @PostMapping("/cliente/envio-email/{idCliente}")
    fun EnviarArquivoPorEmail(
        @PathVariable("idCliente") idCliente: String,
        @Valid @RequestBody body: EnvioArquivoFiscalDTO): ResponseEntity<Any>{
        val consultaCliente: Cliente? = clienteService.retornarClientePorId(idCliente);
        if (consultaCliente == null){
            return  ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            if (consultaCliente.contador?.email == ""){
                return ResponseEntity.badRequest().body("Contador não possui email cadastrado para envio")
            }

            val arquivoEncontrado = clienteService.buscarArquivoEspecifico(consultaCliente, body.ano, body.mes);
            if (arquivoEncontrado == null){
                return ResponseEntity.badRequest().body("Nenhum arquivo encontrado.")
            }

            val assuntoMsg = "Arquivos fiscais - " + consultaCliente.razao +
                    " - " + body.mes + "/" + body.ano.toString();

            val nomeArquivo: String = consultaCliente.cnpj+'_'+body.mes+'_'+body.ano;

            val mensagem = """
                Bom dia,
            
                Segue em anexo os arquivos solicitados.
                Link: ${arquivoEncontrado.link}
                    
                Atenciosamente,
                
                Equipe de Suporte Computek
            """.trimIndent();

            emailService.EnviarEmail(
                consultaCliente.contador!!.email,
                consultaCliente.email ,
                assuntoMsg,
                mensagem,
                "$uploadDir/$nomeArquivo.rar"
                );

            clienteService.atualizarStatusEnviado(
                consultaCliente,
                body.ano,
                body.mes,
                consultaCliente.contador!!.email
            );
            return  ResponseEntity(HttpStatus.OK);
        } catch (ex: Exception){
            return  ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }



}
