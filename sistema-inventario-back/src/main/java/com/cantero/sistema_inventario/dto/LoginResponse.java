package com.cantero.sistema_inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para devolver al frontend después del login exitoso
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String nombre;
    private String email;
    private String rol;
}