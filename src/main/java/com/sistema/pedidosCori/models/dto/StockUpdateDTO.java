package com.sistema.pedidosCori.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateDTO {
    private Long platoId;
    private Integer stockRestante;
    private String nombrePlato;
}
