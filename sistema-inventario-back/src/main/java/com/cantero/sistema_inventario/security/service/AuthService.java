package com.cantero.sistema_inventario.security.service;

import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Usuario;
import com.cantero.sistema_inventario.repository.UsuarioRepository;
import com.cantero.sistema_inventario.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Usuario register(String nombre, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "EMAIL_DUPLICADO",
                    "El correo ya está registrado");
        }

        Usuario user = new Usuario();
        user.setNombre(nombre);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol("USER"); // el registro público nunca decide el rol
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "CREDENCIALES_INVALIDAS",
                    "Email o contraseña incorrectos");
        }

        Usuario user = getUserByEmail(email);
        return jwtTokenProvider.generarToken(user.getEmail());
    }

    public Usuario getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "USUARIO_NO_ENCONTRADO", "Usuario no encontrado"));
    }
}
