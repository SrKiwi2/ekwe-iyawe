package com.sistema.pedidosCori.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "presentaciones_plato")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresentacionPlato {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: Una presentación pertenece a un plato
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plato_id")
    @JsonIgnoreProperties("presentaciones") // 🌟 ESTO ES VITAL
    private Plato plato;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    @Builder.Default
    private Boolean disponible = true;

    @Column(name = "stock_disponible")
    private Integer stockDisponible;
}
