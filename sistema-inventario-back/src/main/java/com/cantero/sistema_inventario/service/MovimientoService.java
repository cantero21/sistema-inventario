package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.MovimientoResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Factura;
import com.cantero.sistema_inventario.model.Movimiento;
import com.cantero.sistema_inventario.model.Producto;
import com.cantero.sistema_inventario.model.TipoMovimiento;
import com.cantero.sistema_inventario.repository.MovimientoRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public MovimientoResponse crearMovimientoEntrada(Long productoId, Integer cantidad, Double precioUnitario) {
        validarCantidad(cantidad);
        Producto producto = obtenerProducto(productoId);

        int nuevoStock = (producto.getStock() == null ? 0 : producto.getStock()) + cantidad;
        producto.setStock(nuevoStock);
        productoRepository.save(producto);

        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipo(TipoMovimiento.ENTRADA)
                .cantidad(cantidad)
                .precioUnitario(precioUnitario)
                .producto(producto)
                .build();

        return MovimientoResponse.from(movimientoRepository.save(movimiento));
    }

    @Transactional
    public MovimientoResponse crearMovimientoSalida(Long productoId, Integer cantidad,
                                                    Double precioUnitario, Factura factura) {
        validarCantidad(cantidad);
        Producto producto = obtenerProducto(productoId);
        int stockActual = producto.getStock() == null ? 0 : producto.getStock();

        if (stockActual < cantidad) {
            throw new ApiException(HttpStatus.CONFLICT, "STOCK_INSUFICIENTE",
                    "Stock insuficiente para el producto: " + producto.getNombre());
        }

        producto.setStock(stockActual - cantidad);
        productoRepository.save(producto);

        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipo(TipoMovimiento.SALIDA)
                .cantidad(cantidad)
                .precioUnitario(precioUnitario)
                .producto(producto)
                .factura(factura)
                .build();

        return MovimientoResponse.from(movimientoRepository.save(movimiento));
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarTodos() {
        return movimientoRepository.findAllByOrderByFechaDesc().stream()
                .map(MovimientoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarPorProducto(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaDesc(productoId).stream()
                .map(MovimientoResponse::from)
                .toList();
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CANTIDAD_INVALIDA",
                    "La cantidad debe ser mayor a 0");
        }
    }

    private Producto obtenerProducto(Long productoId) {
        return productoRepository.findByIdForUpdate(productoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "PRODUCTO_NO_ENCONTRADO", "Producto no encontrado con id " + productoId));
    }
}
