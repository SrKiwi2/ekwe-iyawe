package com.sistema.pedidosCori.models.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistema.pedidosCori.models.entity.Plato;

public interface IPlatoDao extends JpaRepository<Plato, Long> {
    List<Plato> findByDisponibleTrue();
    List<Plato> findByCategoriaAndDisponibleTrue(String categoria);

    // 1. Apaga todos los platos (Para limpiar el menú del día anterior)
    @Modifying
    @Query("UPDATE Plato p SET p.disponible = false")
    void marcarTodosComoNoDisponibles();

    // 2. Enciende solo los platos seleccionados para hoy
    @Modifying
    @Query("UPDATE Plato p SET p.disponible = true WHERE p.id IN :ids")
    void marcarComoDisponibles(@Param("ids") List<Long> ids);

    Optional<Plato> findByNombre(String nombre);
}
