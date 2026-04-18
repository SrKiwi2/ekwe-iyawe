package com.sistema.pedidosCori.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistema.pedidosCori.models.entity.EstadoPedido;
import com.sistema.pedidosCori.models.entity.Pedido;

public interface IPedidoDao extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstadoOrderByFechaCreacionAsc(EstadoPedido estado);
    List<Pedido> findByEstadoInOrderByFechaCreacionAsc(List<EstadoPedido> estados);
    List<Pedido> findByNumeroMesaAndEstadoIn(Integer mesa, List<EstadoPedido> estados);

    List<Pedido> findByPagadoTrue();
}
