package com.cantero.sistema_inventario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String nombre,
        @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
        String direccion,
        @Size(max = 255, message = "El teléfono no puede superar 255 caracteres")
        String telefono,
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 255, message = "El email no puede superar 255 caracteres")
        String email) {
}
