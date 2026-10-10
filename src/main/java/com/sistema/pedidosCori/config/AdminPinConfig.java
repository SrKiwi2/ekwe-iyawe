package com.sistema.pedidosCori.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "admin")
@Getter @Setter
public class AdminPinConfig {
    
    private String pin = "1234";
    private int sessionMinutes = 60;
}
