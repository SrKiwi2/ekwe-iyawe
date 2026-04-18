package com.sistema.pedidosCori.models.dto;

import java.util.List;

import lombok.Data;

@Data
public class PedidoRequestDTO {
    private Integer numeroMesa;
    private String nombreCliente;
    private String tipo;
    private String observaciones;

    private Boolean pagado;
    private String metodoPago;

    private List<DetalleRequestDTO> detalles;

    @Data
    public static class DetalleRequestDTO {
        private Long platoId;
        private Integer cantidad;
        private String notas;
        private Long acompananteSopaId;
        private Long presentacionId;
    }
}
