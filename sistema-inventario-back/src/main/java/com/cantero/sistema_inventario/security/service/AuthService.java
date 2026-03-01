package com.cantero.sistema_inventario.security.service;

import com.cantero.sistema_inventario.model.Usuario;
import com.cantero.sistema_inventario.repository.UsuarioRepository;
import com.cantero.sistema_inventario.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public Usuario register(String nombre, String email, String password, String rol) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }


        if (rol == null || rol.isBlank()) {
            rol = "USER";
        }

        Usuario user = new Usuario();
        user.setNombre(nombre);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol(rol.toUpperCase());

        return userRepository.save(user);
    }


    public String login(String email, String password) {
        Usuario user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }


        return jwtTokenProvider.generarToken(email, user.getRol());
    }



    public Usuario getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
