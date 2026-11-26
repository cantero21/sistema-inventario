package com.cantero.sistema_inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String nombre,
        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion) {
}
