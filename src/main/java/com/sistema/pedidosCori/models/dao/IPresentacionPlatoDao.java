package com.sistema.pedidosCori.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistema.pedidosCori.models.entity.PresentacionPlato;

public interface IPresentacionPlatoDao extends JpaRepository<PresentacionPlato, Long> {
    List<PresentacionPlato> findByPlatoIdOrderByPrecioAsc(Long platoId);

    List<PresentacionPlato> findByPlatoIdAndDisponibleTrueOrderByPrecioAsc(Long platoId);

    @Modifying
    @Query("UPDATE PresentacionPlato pp SET pp.stockDisponible = pp.stockDisponible - :cantidad WHERE pp.id = :id AND pp.stockDisponible >= :cantidad")
    int descontarStockPreciso(@Param("id") Long id, @Param("cantidad") Integer cantidad);
}
