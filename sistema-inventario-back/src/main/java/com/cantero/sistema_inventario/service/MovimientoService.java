package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.model.*;
import com.cantero.sistema_inventario.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;


    @Transactional
    public Movimiento crearMovimientoEntrada(Long productoId, Integer cantidad, Double precioUnitario) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));


        int nuevoStock = (producto.getStock() == null ? 0 : producto.getStock()) + cantidad;
        producto.setStock(nuevoStock);
        productoRepository.save(producto);


        Movimiento m = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipo(TipoMovimiento.ENTRADA)
                .cantidad(cantidad)
                .precioUnitario(precioUnitario)
                .producto(producto)
                .build();

        return movimientoRepository.save(m);
    }


    @Transactional
    public Movimiento crearMovimientoSalida(Long productoId, Integer cantidad, Double precioUnitario, Factura factura) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int stockActual = (producto.getStock() == null ? 0 : producto.getStock());

        if (stockActual < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }


        producto.setStock(stockActual - cantidad);
        productoRepository.save(producto);


        Movimiento m = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipo(TipoMovimiento.SALIDA)
                .cantidad(cantidad)
                .precioUnitario(precioUnitario)
                .producto(producto)
                .factura(factura)
                .build();

        return movimientoRepository.save(m);
    }


    public List<Movimiento> listarTodos() {
        return movimientoRepository.findAllByOrderByFechaDesc();
    }


    public List<Movimiento> listarPorProducto(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaDesc(productoId);
    }
}
