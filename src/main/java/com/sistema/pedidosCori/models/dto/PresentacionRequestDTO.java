package com.sistema.pedidosCori.models.dto;

import com.sistema.pedidosCori.models.entity.Modalidad;

import lombok.Data;

@Data
public class PresentacionRequestDTO {
        private String   nombre;
    private Double   precio;
    private Integer  stockDisponible;
    private Boolean  disponible;
    private Modalidad modalidad;
}
