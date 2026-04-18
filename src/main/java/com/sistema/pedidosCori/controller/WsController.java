package com.sistema.pedidosCori.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WsController {
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String ping(String mensaje) {
        return "pong: " + mensaje;
    }
}
