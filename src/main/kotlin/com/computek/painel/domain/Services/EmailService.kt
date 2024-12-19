package com.computek.painel.domain.Services

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
        emailContador: String,
        emailCliente: String,
        assunto: String,
        msg: String,
        link: String
    ){
        if (emailContador == ""){
           return;
        }

        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true)

        // Configure o remetente com um e-mail válido
        helper.setFrom(emailRemetente)
        helper.setTo(emailContador)
        if (emailCliente != "") {
            helper.setCc(emailCliente)
        }
        helper.setCc(emailRemetente)
        helper.setSubject(assunto)
        helper.setText(msg, false)

        if (link != "") {
            val arquivoAnexo = File(link)
            if (arquivoAnexo.exists()) {
                helper.addAttachment(arquivoAnexo.name, arquivoAnexo)
            } else {
                throw IllegalArgumentException("Arquivo não encontrado: $link")
            }
        }

        // Envia o e-mail
        mailSender.send(mimeMessage)
    }
}
