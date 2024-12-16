package com.computek.painel.domain.Services

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(private val mailSender: JavaMailSender) {

    fun enviarEmail(destinatario: String, assunto: String, mensagem: String) {
        val mailMessage = SimpleMailMessage()
        mailMessage.setTo(destinatario)
        mailMessage.setSubject(assunto)
        mailMessage.setText(mensagem)
        mailSender.send(mailMessage)
    }
}
