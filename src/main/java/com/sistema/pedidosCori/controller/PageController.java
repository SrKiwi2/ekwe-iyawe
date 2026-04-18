package com.sistema.pedidosCori.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

    @GetMapping("/")
    public String home() {
        return "redirect:/mesero";
    }

    @GetMapping("/mesero")
    public String mesero(Model model) {
        
        return "mesero";
    }

    @GetMapping("/cocina")
    public String cocina(Model model) {
        
        return "cocina";
    }
}
