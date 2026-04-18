package com.sistema.pedidosCori.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.pedidosCori.models.dao.IPedidoDao;
import com.sistema.pedidosCori.models.dao.IPlatoDao;
import com.sistema.pedidosCori.models.dao.IPresentacionPlatoDao;
import com.sistema.pedidosCori.models.dto.MenuDiarioItemDTO;
import com.sistema.pedidosCori.models.dto.NotificacionWsDTO;
import com.sistema.pedidosCori.models.dto.PedidoRequestDTO;
import com.sistema.pedidosCori.models.dto.ReporteDiarioDTO;
import com.sistema.pedidosCori.models.dto.StockUpdateDTO;
import com.sistema.pedidosCori.models.entity.DetallePedido;
import com.sistema.pedidosCori.models.entity.EstadoPedido;
import com.sistema.pedidosCori.models.entity.Pedido;
import com.sistema.pedidosCori.models.entity.Plato;
import com.sistema.pedidosCori.models.entity.PresentacionPlato;
import com.sistema.pedidosCori.service.IPedidoService;
import com.sistema.pedidosCori.service.PedidoServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    
    private final IPedidoService        pedidoService;
    private final PedidoServiceImpl     pedidoServiceImpl;
    private final IPedidoDao            pedidoDao;
    private final IPlatoDao             platoDao;
    private final IPresentacionPlatoDao presentacionPlatoDao;
    private final SimpMessagingTemplate messagingTemplate;

    // ── Platos ───────────────────────────────────────────────────────────────
 
    @GetMapping("/platos/todos")
    public ResponseEntity<List<Plato>> getTodosLosPlatos() {
        return ResponseEntity.ok(platoDao.findAll());
    }
 
    @GetMapping("/platos")
    public ResponseEntity<List<Plato>> getPlatos() {
        return ResponseEntity.ok(platoDao.findByDisponibleTrue());
    }
 
    @PostMapping("/platos")
    public ResponseEntity<Plato> crearPlato(@RequestBody Plato plato) {
        plato.setDisponible(true);
        // if (plato.getStockDisponible() == null) plato.setStockDisponible(0);
        return ResponseEntity.ok(platoDao.save(plato));
    }
 
    @PutMapping("/platos/{id}")
    public ResponseEntity<Plato> editarPlato(@PathVariable Long id, @RequestBody Plato d) {
        return platoDao.findById(id).map(p -> {
            p.setNombre(d.getNombre()); p.setPrecio(d.getPrecio()); p.setCategoria(d.getCategoria());
            if (d.getStockDisponible() != null) p.setStockDisponible(d.getStockDisponible());
            return ResponseEntity.ok(platoDao.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }
 
    // ── Presentaciones (tamaños/precios por plato) ───────────────────────────
 
    @GetMapping("/platos/{id}/presentaciones")
    public ResponseEntity<List<PresentacionPlato>> getPresentaciones(@PathVariable Long id) {
        return ResponseEntity.ok(presentacionPlatoDao.findByPlatoIdOrderByPrecioAsc(id));
    }
 
    @PostMapping("/platos/{id}/presentaciones")
    public ResponseEntity<PresentacionPlato> crearPresentacion(
            @PathVariable Long id, @RequestBody PresentacionPlato dto) {
        return platoDao.findById(id).map(plato -> {
            dto.setPlato(plato);
            if (dto.getDisponible() == null) dto.setDisponible(true);
            PresentacionPlato saved = presentacionPlatoDao.save(dto);
            messagingTemplate.convertAndSend("/topic/menu",
                NotificacionWsDTO.builder().tipo("ACTUALIZACION_MENU").mensaje("Presentacion anadida").build());
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
 
    @PutMapping("/presentaciones/{id}")
    public ResponseEntity<PresentacionPlato> editarPresentacion(
            @PathVariable Long id, @RequestBody PresentacionPlato dto) {
        return presentacionPlatoDao.findById(id).map(p -> {
            p.setNombre(dto.getNombre()); p.setPrecio(dto.getPrecio());
            if (dto.getStockDisponible() != null) p.setStockDisponible(dto.getStockDisponible());
            if (dto.getDisponible() != null) p.setDisponible(dto.getDisponible());
            return ResponseEntity.ok(presentacionPlatoDao.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }
 
    @DeleteMapping("/presentaciones/{id}")
    public ResponseEntity<Void> eliminarPresentacion(@PathVariable Long id) {
        presentacionPlatoDao.deleteById(id);
        messagingTemplate.convertAndSend("/topic/menu",
            NotificacionWsDTO.builder().tipo("ACTUALIZACION_MENU").mensaje("Presentacion eliminada").build());
        return ResponseEntity.ok().build();
    }
 
    // ── Pedidos — crear ───────────────────────────────────────────────────────
 
    @PostMapping("/pedidos")
    @Transactional
    public ResponseEntity<Pedido> crearPedido(@RequestBody PedidoRequestDTO dto) {
        Pedido pedido = pedidoService.crearPedido(dto);
 
        Map<Long,Integer> descPlato=new HashMap<>(), descPres=new HashMap<>(), descSopa=new HashMap<>();
        pedido.getDetalles().forEach(d -> {
            if (d.getPresentacion() != null) descPres.merge(d.getPresentacion().getId(), d.getCantidad(), Integer::sum);
            else descPlato.merge(d.getPlato().getId(), d.getCantidad(), Integer::sum);
            if (d.getAcompananteSopa() != null) descSopa.merge(d.getAcompananteSopa().getId(), d.getCantidad(), Integer::sum);
        });
        aplicarDescuentoStock(descPlato, descPres, descSopa);
 
        NotificacionWsDTO notif = NotificacionWsDTO.builder()
            .pedidoId(pedido.getId()).numeroMesa(pedido.getNumeroMesa())
            .nombreCliente(pedido.getNombreCliente()).estado(EstadoPedido.PENDIENTE)
            .mensaje("Nuevo pedido — " + labelPedido(pedido)).tipo("NUEVO_PEDIDO").build();
        messagingTemplate.convertAndSend("/topic/cocina", notif);
        messagingTemplate.convertAndSend("/topic/mesero", notif);
        return ResponseEntity.ok(pedido);
    }
 
    // ── Pedidos — agregar items a comanda abierta ─────────────────────────────
 
    @PostMapping("/pedidos/{id}/detalles")
    @Transactional
    public ResponseEntity<Pedido> agregarDetalles(
            @PathVariable Long id,
            @RequestBody List<PedidoRequestDTO.DetalleRequestDTO> nuevosDetalles) {
 
        Pedido pedido = pedidoService.obtenerPorId(id);
 
        List<DetallePedido> nuevos = nuevosDetalles.stream()
            .map(d -> pedidoServiceImpl.construirDetalle(pedido, d))
            .collect(Collectors.toList());
 
        pedido.getDetalles().addAll(nuevos);
        pedido.setTotal(pedido.getDetalles().stream().mapToDouble(DetallePedido::getSubtotal).sum());
        Pedido saved = pedidoDao.save(pedido);
 
        Map<Long,Integer> descPlato=new HashMap<>(), descPres=new HashMap<>(), descSopa=new HashMap<>();
        nuevos.forEach(d -> {
            if (d.getPresentacion() != null) descPres.merge(d.getPresentacion().getId(), d.getCantidad(), Integer::sum);
            else descPlato.merge(d.getPlato().getId(), d.getCantidad(), Integer::sum);
            if (d.getAcompananteSopa() != null) descSopa.merge(d.getAcompananteSopa().getId(), d.getCantidad(), Integer::sum);
        });
        aplicarDescuentoStock(descPlato, descPres, descSopa);
 
        NotificacionWsDTO notif = NotificacionWsDTO.builder()
            .pedidoId(saved.getId()).numeroMesa(saved.getNumeroMesa())
            .nombreCliente(saved.getNombreCliente()).estado(saved.getEstado())
            .mensaje("Extras agregados — " + labelPedido(saved)).tipo("ITEMS_AGREGADOS").build();
        messagingTemplate.convertAndSend("/topic/cocina", notif);
        messagingTemplate.convertAndSend("/topic/mesero", notif);
        return ResponseEntity.ok(saved);
    }
 
    // ── Estado / activos / pago ───────────────────────────────────────────────
 
    @PutMapping("/pedidos/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, @RequestParam EstadoPedido estado) {
        Pedido pedido = pedidoService.actualizarEstado(id, estado);
        String msg = switch (estado) {
            case EN_PREPARACION -> "En preparacion — " + labelPedido(pedido);
            case LISTO          -> "Listo para servir! — " + labelPedido(pedido);
            case ENTREGADO      -> "Entregado — " + labelPedido(pedido);
            default             -> "Estado actualizado";
        };
        NotificacionWsDTO n = NotificacionWsDTO.builder()
            .pedidoId(pedido.getId()).numeroMesa(pedido.getNumeroMesa())
            .nombreCliente(pedido.getNombreCliente()).estado(estado).mensaje(msg).tipo("ACTUALIZACION_ESTADO").build();
        messagingTemplate.convertAndSend("/topic/mesero", n);
        messagingTemplate.convertAndSend("/topic/cocina", n);
        return ResponseEntity.ok(pedido);
    }
 
    @GetMapping("/pedidos/activos")
    public ResponseEntity<List<Pedido>> pedidosActivos() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosActivos());
    }
 
    @GetMapping("/pedidos/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }
 
    @PutMapping("/pedidos/{id}/pagar")
    public ResponseEntity<Pedido> registrarPago(@PathVariable Long id, @RequestParam String metodoPago) {
        Pedido pedido = pedidoService.registrarPago(id, metodoPago);
        messagingTemplate.convertAndSend("/topic/mesero",
            NotificacionWsDTO.builder().pedidoId(pedido.getId()).estado(pedido.getEstado())
                .tipo("ACTUALIZACION_PAGO").mensaje("Pago recibido: " + labelPedido(pedido)).build());
        return ResponseEntity.ok(pedido);
    }
 
    // ── Menu diario ───────────────────────────────────────────────────────────
 
    @PostMapping("/menu-diario")
    @Transactional
    public ResponseEntity<?> actualizarMenuDiario(@RequestBody List<MenuDiarioItemDTO> items) {
        platoDao.marcarTodosComoNoDisponibles();
        if (items != null) items.forEach(item -> platoDao.findById(item.getId()).ifPresent(p -> {
            p.setDisponible(true);
            p.setStockDisponible(item.getCantidad());
            platoDao.save(p);
            messagingTemplate.convertAndSend("/topic/stock",
                StockUpdateDTO.builder().platoId(p.getId()).stockRestante(p.getStockDisponible()).nombrePlato(p.getNombre()).build());
        }));
        messagingTemplate.convertAndSend("/topic/menu",
            NotificacionWsDTO.builder().tipo("ACTUALIZACION_MENU").mensaje("Menu del dia actualizado").build());
        return ResponseEntity.ok(Collections.singletonMap("status", "Menu actualizado"));
    }
 
    // ── Reportes ─────────────────────────────────────────────────────────────
 
    @GetMapping("/reportes/diarios")
    public ResponseEntity<List<ReporteDiarioDTO>> getReportesDiarios() {
        List<Pedido> pagados = pedidoService.obtenerTodosLosPagados();
        Map<LocalDate,List<Pedido>> porFecha = pagados.stream()
            .collect(Collectors.groupingBy(p -> p.getFechaCreacion().toLocalDate()));
        List<ReporteDiarioDTO> reportes = new ArrayList<>();
        porFecha.entrySet().stream().sorted(Map.Entry.<LocalDate,List<Pedido>>comparingByKey().reversed())
            .forEach(entry -> {
                List<Pedido> dias = entry.getValue();
                double total = dias.stream().mapToDouble(p -> p.getDetalles().stream().mapToDouble(DetallePedido::getSubtotal).sum()).sum();
                Map<String,int[]> itemMap = new HashMap<>();
                Map<String,double[]> precioMap = new HashMap<>();
                dias.forEach(p -> p.getDetalles().forEach(d -> {
                    String key = d.getPresentacion() != null
                        ? d.getPlato().getNombre() + " (" + d.getPresentacion().getNombre() + ")"
                        : d.getPlato().getNombre();
                    itemMap.computeIfAbsent(key, k -> new int[]{0})[0] += d.getCantidad();
                    precioMap.put(key, new double[]{d.getPrecioUnitario()});
                }));
                List<ReporteDiarioDTO.ItemReporteDTO> items = new ArrayList<>();
                itemMap.forEach((nombre, qtys) -> {
                    double precio = precioMap.getOrDefault(nombre, new double[]{0})[0];
                    ReporteDiarioDTO.ItemReporteDTO item = new ReporteDiarioDTO.ItemReporteDTO();
                    item.setNombre(nombre); item.setCantidad(qtys[0]); item.setSubtotal(precio * qtys[0]);
                    items.add(item);
                });
                items.sort((a,b) -> b.getCantidad() - a.getCantidad());
                ReporteDiarioDTO r = new ReporteDiarioDTO();
                r.setFecha(entry.getKey()); r.setTotalVentas(total); r.setCantidadPedidos(dias.size());
                r.setPedidosPagados((int) dias.stream().filter(Pedido::getPagado).count());
                r.setItems(items); reportes.add(r);
            });
        return ResponseEntity.ok(reportes);
    }
 
    // ── Helpers ──────────────────────────────────────────────────────────────
 
    private String labelPedido(Pedido p) {
        return p.getNumeroMesa() != null ? "Mesa " + p.getNumeroMesa() : p.getNombreCliente();
    }
 
    private void aplicarDescuentoStock(Map<Long,Integer> descPlato, Map<Long,Integer> descPres, Map<Long,Integer> descSopa) {
        descPlato.forEach((pid,q) -> platoDao.findById(pid).ifPresent(p -> {
            if (p.getStockDisponible() != null) {
                int n = Math.max(0, p.getStockDisponible()-q); p.setStockDisponible(n); platoDao.save(p);
                messagingTemplate.convertAndSend("/topic/stock", StockUpdateDTO.builder().platoId(p.getId()).stockRestante(n).nombrePlato(p.getNombre()).build());
            }
        }));
        descPres.forEach((presId,q) -> presentacionPlatoDao.findById(presId).ifPresent(pres -> {
            if (pres.getStockDisponible() != null) {
                int n = Math.max(0, pres.getStockDisponible()-q); pres.setStockDisponible(n); presentacionPlatoDao.save(pres);
                messagingTemplate.convertAndSend("/topic/stock", StockUpdateDTO.builder().platoId(pres.getPlato().getId()).stockRestante(n).nombrePlato(pres.getPlato().getNombre()+" – "+pres.getNombre()).build());
            }
        }));
        descSopa.forEach((sid,q) -> platoDao.findById(sid).ifPresent(s -> {
            if (s.getStockDisponible() != null) {
                int n = Math.max(0, s.getStockDisponible()-q); s.setStockDisponible(n); platoDao.save(s);
                messagingTemplate.convertAndSend("/topic/stock", StockUpdateDTO.builder().platoId(s.getId()).stockRestante(n).nombrePlato(s.getNombre()).build());
            }
        }));
    }
}
