package com.computek.painel.application.Controllers

import com.computek.painel.application.DTOs.*
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
import org.springframework.web.bind.annotation.*
import java.nio.file.Files
import java.nio.file.Paths
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated

@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
@Validated
class ClienteController(private val clienteService: ClienteService) {

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
    fun getClientsByCNPJ(@PathVariable("cnpj") cnpj: String): ResponseEntity<ApiResponse<Cliente>> {
        val retorno = clienteService.retornarClientePorCNPJ(cnpj);
        return ResponseEntity.status(retorno.status).body(retorno);
    }

    @PostMapping("/cliente")
    fun createClient(@Valid @RequestBody novoCliente: Cliente): ResponseEntity<ApiResponse<Cliente>> {
        val retorno = clienteService.salvarCliente(novoCliente)
        return ResponseEntity.status(retorno.status).body(retorno);
    }

    @PutMapping("/cliente/{cnpj}")
    fun updateClientByCNPJ(@Valid @PathVariable("cnpj") cnpj: String,
                           @Valid  @RequestBody body: Cliente):ResponseEntity<ApiResponse<Cliente>> {
        val consultaCliente =  clienteService.consultarCliente(cnpj)
        if (consultaCliente == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(clienteService.createApiResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = "Cliente não encontrado!"
            ));
        }
        val retorno = clienteService.salvarCliente(body)
        return ResponseEntity.status(retorno.status).body(retorno);
    }

    @PostMapping("/cliente/upload/{idCliente}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(
                    @Valid
                    @PathVariable("idCliente") idCliente: String,
                    @Valid
                    @ModelAttribute params: RequestUploadDTO,
                    request: HttpServletRequest): ResponseEntity<ApiResponse<ResponseUploadDTO>> {
        val responseUpload = clienteService.uploadArquivoCliente(
            idCliente,
            params.file,
            params.mes,
            params.ano,
            request
        )
        return ResponseEntity.status(responseUpload.status).body(responseUpload);
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
        @Valid
        @PathVariable("idCliente") idCliente: String,
        @Valid @RequestBody body: RequestEnvioEmailDTO): ResponseEntity<ApiResponse<Any>>{
        val retorno = clienteService.EnviarArquivoNoEmail(idCliente, body);
        return ResponseEntity.status(retorno.status).body(retorno);
    }

    @DeleteMapping("/cliente/arquivo/{idCliente}")
    fun DeletarArquivo (
        @PathVariable("idCliente") idCliente: String,
        @Valid
        @RequestBody body: RequestDeleteArquivoDTO
    ):ResponseEntity<ApiResponse<ResponseUploadDTO>>{
        val retorno = clienteService.deleteArquivoCliente(
            idCliente,
            body.mes,
            body.ano
        )
        return ResponseEntity.status(retorno.status).body(retorno);
    }

    @PostMapping("/cliente/status/{idCliente}")
    fun AlternarStatus (
        @PathVariable("idCliente") idCliente: String
    ):ResponseEntity<ApiResponse<Any>>{
        val retorno = clienteService.AlternarStatusCliente(
            idCliente
        )
        return ResponseEntity.status(retorno.status).body(retorno);
    }
}
