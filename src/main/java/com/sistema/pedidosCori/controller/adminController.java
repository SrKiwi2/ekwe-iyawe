package com.sistema.pedidosCori.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sistema.pedidosCori.config.AdminInterceptor;
import com.sistema.pedidosCori.config.AdminPinConfig;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class adminController {

    private final AdminPinConfig pinConfig;

    @GetMapping("/menu")
    public String vistaMenu() {
        return "admin-menu";
    }

    /**
     * Verificar PIN desde el modal del mesero.
     * POST /api/admin/verificar-pin
     * Body: { "pin": "1234" }
     *
     * Responde 200 { ok: true }  → JS redirige a /menu
     * Responde 401 { ok: false } → JS muestra error en el modal
     */
    @PostMapping("/api/admin/verificar-pin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verificarPin(
            @RequestBody Map<String, String> body,
            HttpSession session) {
 
        String pinRecibido = body.getOrDefault("pin", "");
 
        if (pinConfig.getPin().equals(pinRecibido)) {
            // Marca la sesión como admin autenticado
            session.setAttribute(AdminInterceptor.SESSION_KEY, Boolean.TRUE);
            session.setMaxInactiveInterval(pinConfig.getSessionMinutes() * 60);
            return ResponseEntity.ok(Map.of("ok", true));
        }
 
        return ResponseEntity.status(401).body(Map.of("ok", false, "msg", "PIN incorrecto"));
    }
 
    /**
     * Cerrar sesión admin.
     * POST /api/admin/logout
     */
    @PostMapping("/api/admin/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.removeAttribute(AdminInterceptor.SESSION_KEY);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
