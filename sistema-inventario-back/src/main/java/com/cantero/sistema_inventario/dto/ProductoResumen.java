package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Producto;

public record ProductoResumen(Long id, String nombre, Double precio, Integer stock) {

    public static ProductoResumen from(Producto producto) {
        return producto == null ? null
                : new ProductoResumen(producto.getId(), producto.getNombre(),
                        producto.getPrecio(), producto.getStock());
    }
}
