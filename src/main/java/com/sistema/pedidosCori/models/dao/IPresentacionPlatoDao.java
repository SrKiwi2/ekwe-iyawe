package com.sistema.pedidosCori.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistema.pedidosCori.models.entity.PresentacionPlato;

public interface IPresentacionPlatoDao extends JpaRepository<PresentacionPlato, Long> {
    List<PresentacionPlato> findByPlatoIdOrderByPrecioAsc(Long platoId);

    List<PresentacionPlato> findByPlatoIdAndDisponibleTrueOrderByPrecioAsc(Long platoId);
}
