package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Cliente;

public record ClienteResponse(Long id, String nombre, String direccion, String telefono, String email) {

    public static ClienteResponse from(Cliente cliente) {
        return cliente == null ? null
                : new ClienteResponse(cliente.getId(), cliente.getNombre(), cliente.getDireccion(),
                        cliente.getTelefono(), cliente.getEmail());
    }
}
