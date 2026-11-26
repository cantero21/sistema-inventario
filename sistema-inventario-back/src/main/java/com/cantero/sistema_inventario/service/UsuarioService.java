package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.UsuarioRequest;
import com.cantero.sistema_inventario.dto.UsuarioResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Usuario;
import com.cantero.sistema_inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse getById(Long id) {
        return UsuarioResponse.from(obtenerUsuario(id));
    }

    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "PASSWORD_REQUERIDA",
                    "La contraseña es obligatoria para crear un usuario");
        }
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "EMAIL_DUPLICADO",
                    "El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(normalizarRol(request.getRol()));
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario existing = obtenerUsuario(id);

        usuarioRepository.findByEmail(request.getEmail())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new ApiException(HttpStatus.CONFLICT, "EMAIL_DUPLICADO",
                            "El correo ya está registrado por otro usuario");
                });

        existing.setNombre(request.getNombre());
        existing.setEmail(request.getEmail());
        existing.setRol(normalizarRol(request.getRol()));
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return UsuarioResponse.from(usuarioRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw noEncontrado(id);
        }
        usuarioRepository.deleteById(id);
    }

    private String normalizarRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return "USER";
        }
        String normalizado = rol.trim().toUpperCase(Locale.ROOT);
        if (!normalizado.equals("ADMIN") && !normalizado.equals("USER")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ROL_INVALIDO",
                    "El rol debe ser ADMIN o USER");
        }
        return normalizado;
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
    }

    private ApiException noEncontrado(Long id) {
        return new ApiException(HttpStatus.NOT_FOUND, "USUARIO_NO_ENCONTRADO",
                "Usuario no encontrado con id " + id);
    }
}
