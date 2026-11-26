package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Proveedor;

public record ProveedorResumen(Long id, String nombre) {

    public static ProveedorResumen from(Proveedor proveedor) {
        return proveedor == null ? null : new ProveedorResumen(proveedor.getId(), proveedor.getNombre());
    }
}
