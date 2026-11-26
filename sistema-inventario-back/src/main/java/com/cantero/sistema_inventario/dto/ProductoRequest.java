package com.cantero.sistema_inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String nombre,
        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion,
        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a 0")
        Double precio,
        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stock,
        @Positive(message = "La categoría debe ser un id válido")
        Long categoriaId,
        @Positive(message = "El proveedor debe ser un id válido")
        Long proveedorId) {
}
