package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Movimiento;
import com.cantero.sistema_inventario.model.TipoMovimiento;

import java.time.LocalDateTime;

public record MovimientoResponse(
        Long id,
        LocalDateTime fecha,
        TipoMovimiento tipo,
        Integer cantidad,
        Double precioUnitario,
        ProductoResumen producto) {

    public static MovimientoResponse from(Movimiento movimiento) {
        return movimiento == null ? null
                : new MovimientoResponse(movimiento.getId(), movimiento.getFecha(), movimiento.getTipo(),
                        movimiento.getCantidad(), movimiento.getPrecioUnitario(),
                        ProductoResumen.from(movimiento.getProducto()));
    }
}
