package com.cantero.sistema_inventario.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record MovimientoRequest(
        @NotNull(message = "El producto es obligatorio")
        @Positive(message = "El producto debe ser un id válido")
        Long productoId,
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        @Max(value = 1000000, message = "La cantidad no puede superar 1000000")
        Integer cantidad,
        @NotNull(message = "El precio unitario es obligatorio")
        @PositiveOrZero(message = "El precio unitario no puede ser negativo")
        Double precioUnitario) {
}
