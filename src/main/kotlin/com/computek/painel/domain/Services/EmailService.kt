package com.computek.painel.domain.Services

import com.computek.painel.domain.Entities.Arquivo
import com.computek.painel.domain.Entities.Cliente
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.File

@Service
class EmailService(private val mailSender: JavaMailSender) {


    @Value("\${spring.mail.username}")
    private lateinit var emailRemetente: String

    fun EnviarEmail(
        cliente: Cliente,
        arquivo: Arquivo
    ) : Boolean{
        if ((cliente.contador == null) and (cliente.email == null)){
            return false;
        }

        val assuntoMsg = "Arquivos fiscais - " + cliente.razao +
                " - " + arquivo.mes + "/" + arquivo.ano.toString();

        val nomeArquivo: String = cliente.cnpj+'_'+arquivo.mes+'_'+arquivo.ano;

        val mensagem = """
                Olá, tudo bem?
            
                Segue em anexo os arquivos solicitados.
                Link: ${arquivo.link}
                    
                Atenciosamente,
                
                Equipe de Suporte Computek
            """.trimIndent();

        try {

            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, true)

            // Configure o remetente com um e-mail válido
            helper.setFrom(emailRemetente)
            helper.setTo(cliente.contador!!.email)
            if (cliente.email != "") {
                helper.setCc(cliente.email)
            }
            helper.setCc(emailRemetente)
            helper.setSubject(assuntoMsg)
            helper.setText(mensagem, false)

            if (arquivo.link != "") {
                val arquivoAnexo = File(arquivo.link)
                if (arquivoAnexo.exists()) {
                    helper.addAttachment(arquivoAnexo.name, arquivoAnexo)
                } else {
                    throw IllegalArgumentException("Arquivo não encontrado: ${arquivo.link}")
                }
            }

            // Envia o e-mail
            mailSender.send(mimeMessage)
            return true
        } catch (ex: Exception){
            return false;
        }
    }
}
