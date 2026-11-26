package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.FacturaItem;

public record FacturaItemResponse(
        Long id,
        Integer cantidad,
        Double precioUnitario,
        ProductoResumen producto) {

    public static FacturaItemResponse from(FacturaItem item) {
        return item == null ? null
                : new FacturaItemResponse(item.getId(), item.getCantidad(), item.getPrecioUnitario(),
                        ProductoResumen.from(item.getProducto()));
    }
}
