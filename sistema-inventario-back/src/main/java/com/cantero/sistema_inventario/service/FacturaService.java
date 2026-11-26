package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.FacturaItemRequest;
import com.cantero.sistema_inventario.dto.FacturaRequest;
import com.cantero.sistema_inventario.dto.FacturaResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Cliente;
import com.cantero.sistema_inventario.model.EstadoFactura;
import com.cantero.sistema_inventario.model.Factura;
import com.cantero.sistema_inventario.model.FacturaItem;
import com.cantero.sistema_inventario.model.Producto;
import com.cantero.sistema_inventario.model.Usuario;
import com.cantero.sistema_inventario.repository.ClienteRepository;
import com.cantero.sistema_inventario.repository.FacturaRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import com.cantero.sistema_inventario.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoService movimientoService;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<FacturaResponse> getAll() {
        return facturaRepository.findAllWithItems().stream()
                .map(FacturaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FacturaResponse getById(Long id) {
        return FacturaResponse.from(obtenerFactura(id));
    }

    @Transactional
    public FacturaResponse save(FacturaRequest request) {
        validarProductosUnicos(request.items());

        Factura factura = new Factura();
        factura.setFecha(request.fecha() == null ? LocalDate.now() : request.fecha());
        factura.setCliente(obtenerCliente(request.clienteId()));
        factura.setUsuario(usuarioAutenticado());

        List<FacturaItemRequest> itemsOrdenados = request.items().stream()
                .sorted(Comparator.comparing(FacturaItemRequest::productoId))
                .toList();

        Map<Long, FacturaItem> itemsPorProducto = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (FacturaItemRequest itemRequest : itemsOrdenados) {
            Producto producto = obtenerProducto(itemRequest.productoId());
            BigDecimal precioUnitario = precioDeVenta(producto);
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(itemRequest.cantidad()));
            total = total.add(subtotal);

            FacturaItem item = FacturaItem.builder()
                    .producto(producto)
                    .cantidad(itemRequest.cantidad())
                    .precioUnitario(precioUnitario.doubleValue())
                    .build();
            item.setFactura(factura);
            itemsPorProducto.put(itemRequest.productoId(), item);
        }

        List<FacturaItem> items = new ArrayList<>();
        for (FacturaItemRequest itemRequest : request.items()) {
            items.add(itemsPorProducto.get(itemRequest.productoId()));
        }
        factura.setItems(items);
        factura.setTotal(total.doubleValue());

        Factura savedFactura = facturaRepository.save(factura);

        for (FacturaItemRequest itemRequest : itemsOrdenados) {
            FacturaItem item = itemsPorProducto.get(itemRequest.productoId());
            movimientoService.crearMovimientoSalida(
                    item.getProducto().getId(),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    savedFactura
            );
        }

        Long id = savedFactura.getId();
        facturaRepository.flush();
        entityManager.clear();

        return getById(id);
    }

    @Transactional
    public FacturaResponse update(Long id, FacturaRequest request) {
        throw new ApiException(HttpStatus.CONFLICT, "FACTURA_INMUTABLE",
                "La edición de una factura emitida no está permitida; anule y cree una nueva");
    }

    @Transactional
    public void anularFactura(Long id) {
        Factura factura = obtenerFactura(id);

        if (factura.getEstado() == EstadoFactura.ANULADA) {
            throw new ApiException(HttpStatus.CONFLICT, "FACTURA_YA_ANULADA",
                    "La factura con id " + id + " ya está anulada");
        }

        Usuario usuario = usuarioAutenticado();

        if (factura.getItems() != null) {
            factura.getItems().stream()
                    .sorted(Comparator.comparing((FacturaItem item) -> item.getProducto().getId()))
                    .forEach(item -> movimientoService.crearMovimientoEntrada(
                            item.getProducto().getId(),
                            item.getCantidad(),
                            item.getPrecioUnitario()
                    ));
        }

        factura.setEstado(EstadoFactura.ANULADA);
        factura.setFechaAnulacion(LocalDateTime.now());
        factura.setUsuarioAnulacion(usuario);
        facturaRepository.save(factura);
    }

    @Transactional
    public void delete(Long id) {
        throw new ApiException(HttpStatus.CONFLICT, "FACTURA_NO_ELIMINABLE",
                "Las facturas no se pueden eliminar; anule la factura en su lugar");
    }

    private void validarProductosUnicos(List<FacturaItemRequest> items) {
        Set<Long> productoIds = new HashSet<>();
        for (FacturaItemRequest item : items) {
            if (!productoIds.add(item.productoId())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "PRODUCTO_DUPLICADO",
                        "El producto con id " + item.productoId() + " está repetido en la factura");
            }
        }
    }

    private BigDecimal precioDeVenta(Producto producto) {
        if (producto.getPrecio() == null) {
            throw new ApiException(HttpStatus.CONFLICT, "PRODUCTO_SIN_PRECIO",
                    "El producto " + producto.getNombre() + " no tiene precio de venta configurado");
        }
        return BigDecimal.valueOf(producto.getPrecio());
    }

    private Cliente obtenerCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "CLIENTE_NO_ENCONTRADO", "Cliente no encontrado con id " + clienteId));
    }

    private Producto obtenerProducto(Long productoId) {
        return productoRepository.findByIdForUpdate(productoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "PRODUCTO_NO_ENCONTRADO", "Producto no encontrado con id " + productoId));
    }

    private Factura obtenerFactura(Long id) {
        return facturaRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "FACTURA_NO_ENCONTRADA", "Factura no encontrada con id " + id));
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "NO_AUTENTICADO",
                    "Debes iniciar sesión");
        }
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        "USUARIO_NO_ENCONTRADO", "Usuario no encontrado"));
    }
}
