package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.dto.LoginRequest;
import com.cantero.sistema_inventario.dto.LoginResponse;
import com.cantero.sistema_inventario.dto.RegisterRequest;
import com.cantero.sistema_inventario.dto.UsuarioResponse;
import com.cantero.sistema_inventario.model.Usuario;
import com.cantero.sistema_inventario.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Registro de nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = authService.register(
                request.getNombre(),
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(usuario));
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
