package com.sistema.pedidosCori.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.pedidosCori.models.dao.IPedidoDao;
import com.sistema.pedidosCori.models.dao.IPlatoDao;
import com.sistema.pedidosCori.models.dao.IPresentacionPlatoDao;
import com.sistema.pedidosCori.models.dto.PedidoRequestDTO;
import com.sistema.pedidosCori.models.dto.StockUpdateDTO;
import com.sistema.pedidosCori.models.entity.DetallePedido;
import com.sistema.pedidosCori.models.entity.EstadoPedido;
import com.sistema.pedidosCori.models.entity.Pedido;
import com.sistema.pedidosCori.models.entity.Plato;
import com.sistema.pedidosCori.models.entity.PresentacionPlato;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService{

    private final IPedidoDao            pedidoDao;
    private final IPlatoDao             platoDao;
    private final IPresentacionPlatoDao presentacionPlatoDao;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public Pedido crearPedido(PedidoRequestDTO dto) {
        Pedido pedido = Pedido.builder()
            .numeroMesa(dto.getNumeroMesa())
            .nombreCliente(dto.getNombreCliente())
            .tipo(dto.getTipo())
            .observaciones(dto.getObservaciones())
            .estado(EstadoPedido.PENDIENTE)
            .pagado(dto.getPagado() != null ? dto.getPagado() : false)
            .metodoPago(dto.getMetodoPago())
            .build();
 
        List<DetallePedido> detalles = dto.getDetalles().stream()
            .map(d -> construirDetalle(pedido, d))
            .collect(Collectors.toList());
 
        double total = detalles.stream().mapToDouble(DetallePedido::getSubtotal).sum();
        pedido.setDetalles(detalles);
        pedido.setTotal(total);
        return pedidoDao.save(pedido);
    }
 
    /**
     * Construye un DetallePedido desde el DTO.
     * - presentacionId != null → usa presentacion.precio (refresco con tamaño elegido)
     * - presentacionId == null → usa plato.precio (sopas, segundos, extras, etc.)
     * El stock NO se descuenta aquí — lo hace ApiController de forma agregada.
     */
    public DetallePedido construirDetalle(Pedido pedido, PedidoRequestDTO.DetalleRequestDTO d) {
        Plato plato = platoDao.findById(d.getPlatoId())
            .orElseThrow(() -> new RuntimeException("Plato no encontrado: " + d.getPlatoId()));
 
        Plato acompSopa = null;
        if (d.getAcompananteSopaId() != null) {
            acompSopa = platoDao.findById(d.getAcompananteSopaId()).orElse(null);
        }
 
        PresentacionPlato presentacion = null;
        if (d.getPresentacionId() != null) {
            presentacion = presentacionPlatoDao.findById(d.getPresentacionId()).orElse(null);
        }
 
        Double precio = (presentacion != null) ? presentacion.getPrecio() : plato.getPrecio();
 
        return DetallePedido.builder()
            .pedido(pedido)
            .plato(plato)
            .cantidad(d.getCantidad())
            .precioUnitario(precio)
            .subtotal(precio * d.getCantidad())
            .notas(d.getNotas())
            .acompananteSopa(acompSopa)
            .presentacion(presentacion)
            .build();
    }
 
    @Override
    @Transactional
    public Pedido actualizarEstado(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPorId(pedidoId);
        pedido.setEstado(nuevoEstado);
        return pedidoDao.save(pedido);
    }
 
    @Override
    public List<Pedido> obtenerPedidosActivos() {
        return pedidoDao.findByEstadoInOrderByFechaCreacionAsc(
            List.of(EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO));
    }
 
    @Override
    public List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado) {
        return pedidoDao.findByEstadoOrderByFechaCreacionAsc(estado);
    }
 
    @Override
    public Pedido obtenerPorId(Long id) {
        return pedidoDao.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));
    }
 
    @Override
    public Pedido registrarPago(Long id, String metodoPago) {
        Pedido pedido = obtenerPorId(id);
        pedido.setPagado(true);
        pedido.setMetodoPago(metodoPago);
        return pedidoDao.save(pedido);
    }
 
    @Override
    public List<Pedido> obtenerTodosLosPagados() {
        return pedidoDao.findByPagadoTrue();
    }
}
