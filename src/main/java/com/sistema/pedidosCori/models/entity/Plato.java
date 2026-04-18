package com.sistema.pedidosCori.models.entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "platos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plato {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    private String categoria;

    private Boolean disponible = true;

    @Column(name = "stock_disponible")
    private Integer stockDisponible;

    // Agregar esto en la clase Plato
    @OneToMany(mappedBy = "plato", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("plato") // 🌟 ESTO ES VITAL: Evita el bucle infinito en el JSON
    private List<PresentacionPlato> presentaciones = new ArrayList<>();
}
