package com.sistema.pedidosCori.models.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ReporteDiarioDTO {
    private LocalDate fecha;
    private Double totalVentas;
    private Integer cantidadPedidos;
    private Integer pedidosPagados;
    private List<ItemReporteDTO> items;
 
    @Setter @Getter
    public static class ItemReporteDTO {
        private String nombre;
        private Integer cantidad;
        private Double subtotal;
    }
}
