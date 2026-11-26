package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Proveedor;

public record ProveedorResponse(Long id, String nombre, String direccion, String telefono, String email) {

    public static ProveedorResponse from(Proveedor proveedor) {
        return proveedor == null ? null
                : new ProveedorResponse(proveedor.getId(), proveedor.getNombre(), proveedor.getDireccion(),
                        proveedor.getTelefono(), proveedor.getEmail());
    }
}
