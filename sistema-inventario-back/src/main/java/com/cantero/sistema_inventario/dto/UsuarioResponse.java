package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Usuario;

public record UsuarioResponse(Long id, String nombre, String email, String rol) {

    public static UsuarioResponse from(Usuario usuario) {
        return usuario == null ? null
                : new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}
