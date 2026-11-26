package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Producto;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        Double precio,
        Integer stock,
        CategoriaResumen categoria,
        ProveedorResumen proveedor) {

    public static ProductoResponse from(Producto producto) {
        return producto == null ? null
                : new ProductoResponse(producto.getId(), producto.getNombre(), producto.getDescripcion(),
                        producto.getPrecio(), producto.getStock(),
                        CategoriaResumen.from(producto.getCategoria()),
                        ProveedorResumen.from(producto.getProveedor()));
    }
}
