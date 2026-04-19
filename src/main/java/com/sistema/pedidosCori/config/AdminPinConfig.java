package com.sistema.pedidosCori.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "admin")
@Getter @Setter
public class AdminPinConfig {
    
    /** Configura en application.properties: admin.pin=1234 */
    private String pin = "1234";
    /** Minutos que dura la sesión admin antes de pedir PIN de nuevo */
    private int sessionMinutes = 60;
}
