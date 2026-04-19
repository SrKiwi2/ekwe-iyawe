package com.sistema.pedidosCori.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sistema.pedidosCori.models.entity.EstadoPedido;
import com.sistema.pedidosCori.models.entity.Pedido;

public interface IPedidoDao extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstadoOrderByFechaCreacionAsc(EstadoPedido estado);
    List<Pedido> findByEstadoInOrderByFechaCreacionAsc(List<EstadoPedido> estados);

    List<Pedido> findByEstadoInAndPagadoFalseOrderByFechaCreacionAsc(List<EstadoPedido> estados);
    List<Pedido> findByNumeroMesaAndEstadoIn(Integer mesa, List<EstadoPedido> estados);

    List<Pedido> findByPagadoTrue();

    @Query("SELECT DISTINCT p.numeroMesa FROM Pedido p " +
        "WHERE p.estado IN :estados " +
        "AND p.pagado = false " +
        "AND p.numeroMesa IS NOT NULL")
    List<Integer> findMesasOcupadas(@Param("estados") List<EstadoPedido> estados);
}
