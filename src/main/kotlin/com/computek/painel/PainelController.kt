package com.computek.painel

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/painel")
class PainelController {

    @GetMapping("/mensagem")
    fun getMessagem() : String{
        return  "Hello world 2!!"
    }
}