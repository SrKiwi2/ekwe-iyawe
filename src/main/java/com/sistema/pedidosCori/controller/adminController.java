package com.sistema.pedidosCori.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class adminController {

    @GetMapping("/menu")
    public String vistaMenu() {
        return "admin-menu";
    }
}
