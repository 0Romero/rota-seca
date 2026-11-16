package br.com.fiap.rotaseca.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Quem abre a URL do deploy cai direto na documentação interativa (Swagger UI). */
@Controller
@Hidden
public class HomeController {

    @GetMapping("/")
    public String inicio() {
        return "redirect:/swagger-ui.html";
    }
}
