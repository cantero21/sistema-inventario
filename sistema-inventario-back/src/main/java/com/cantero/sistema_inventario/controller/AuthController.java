package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.dto.LoginRequest;
import com.cantero.sistema_inventario.dto.LoginResponse;
import com.cantero.sistema_inventario.dto.RegisterRequest;
import com.cantero.sistema_inventario.model.Usuario;
import com.cantero.sistema_inventario.security.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Registro de nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@Valid @RequestBody RegisterRequest request) {
        // Si no envían rol, se asigna "USER" por defecto
        String rol = (request.getRol() != null) ? request.getRol() : "USER";

        Usuario usuario = authService.register(
                request.getNombre(),
                request.getEmail(),
                request.getPassword(),
                rol
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        Usuario user = authService.getUserByEmail(request.getEmail());


        LoginResponse response = LoginResponse.builder()
                .token(token)
                .nombre(user.getNombre())
                .email(user.getEmail())
                .rol(user.getRol())
                .build();

        return ResponseEntity.ok(response);
    }
}