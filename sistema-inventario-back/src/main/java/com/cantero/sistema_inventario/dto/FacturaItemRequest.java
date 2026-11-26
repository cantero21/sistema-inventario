package com.cantero.sistema_inventario.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FacturaItemRequest(
        @NotNull(message = "El producto es obligatorio")
        @Positive(message = "El producto debe ser un id válido")
        Long productoId,
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        @Max(value = 1000000, message = "La cantidad no puede superar 1000000")
        Integer cantidad) {
}
