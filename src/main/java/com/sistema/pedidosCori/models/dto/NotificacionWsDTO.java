package com.sistema.pedidosCori.models.dto;

import com.sistema.pedidosCori.models.entity.EstadoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionWsDTO {
    private Long pedidoId;
    private Integer numeroMesa;
    private String nombreCliente;
    private EstadoPedido estado;
    private String mensaje;
    private String tipo;
}
