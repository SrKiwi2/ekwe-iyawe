package com.sistema.pedidosCori.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor{
    
    public static final String SESSION_KEY = "ADMIN_AUTH";
 
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
            throws Exception {
 
        HttpSession session = req.getSession(false);
        if (session != null && Boolean.TRUE.equals(session.getAttribute(SESSION_KEY))) {
            return true; // sesión válida → permitir
        }
 
        // No autenticado → redirigir al mesero con flag ?admin=1
        // El mesero detecta ese param y abre el modal de PIN automáticamente
        res.sendRedirect("/mesero?admin=1");
        return false;
    }
}
