package com.cantero.sistema_inventario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO para recibir las credenciales de login
@Data
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}