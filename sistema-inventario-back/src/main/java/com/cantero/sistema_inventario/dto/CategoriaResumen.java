package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Categoria;

public record CategoriaResumen(Long id, String nombre) {

    public static CategoriaResumen from(Categoria categoria) {
        return categoria == null ? null : new CategoriaResumen(categoria.getId(), categoria.getNombre());
    }
}
