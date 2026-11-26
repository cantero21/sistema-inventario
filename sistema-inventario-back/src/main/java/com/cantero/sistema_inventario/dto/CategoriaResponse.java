package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Categoria;

public record CategoriaResponse(Long id, String nombre, String descripcion) {

    public static CategoriaResponse from(Categoria categoria) {
        return categoria == null ? null
                : new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getDescripcion());
    }
}
