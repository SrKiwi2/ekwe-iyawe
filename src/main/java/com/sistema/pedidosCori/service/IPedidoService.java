package com.sistema.pedidosCori.service;

import java.util.List;

import com.sistema.pedidosCori.models.dto.PedidoRequestDTO;
import com.sistema.pedidosCori.models.entity.EstadoPedido;
import com.sistema.pedidosCori.models.entity.Pedido;

public interface IPedidoService {
    Pedido crearPedido(PedidoRequestDTO dto);
    Pedido actualizarEstado(Long pedidoId, EstadoPedido nuevoEstado);
    Pedido registrarPago(Long id, String metodoPago);
    List<Pedido> obtenerPedidosActivos();
    List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado);
    Pedido obtenerPorId(Long id);
    List<Pedido> obtenerTodosLosPagados();
}
