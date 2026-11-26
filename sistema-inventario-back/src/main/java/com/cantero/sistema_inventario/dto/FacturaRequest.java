package com.cantero.sistema_inventario.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record FacturaRequest(
        LocalDate fecha,
        @NotNull(message = "El cliente es obligatorio")
        @Positive(message = "El cliente debe ser un id válido")
        Long clienteId,
        @NotEmpty(message = "La factura debe tener al menos un ítem")
        @Valid
        List<FacturaItemRequest> items) {
}
