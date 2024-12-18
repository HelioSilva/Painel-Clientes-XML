package com.computek.painel.domain.Services

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.File

@Service
class EmailService(private val mailSender: JavaMailSender) {
    fun EnviarEmail(
        emailContador: String,
        emailCliente: String,
        assunto: String,
        msg: String,
        link: String
    ){
        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true)

        // Configure o remetente com um e-mail válido
        helper.setFrom("nfe@computek.tech")
        helper.setTo(emailContador)
        helper.setCc(emailCliente)
        helper.setCc("nfe@computek.tech")
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
