package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.model.Usuario;
import com.cantero.sistema_inventario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    public Usuario getById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }


    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario update(Long id, Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(usuario.getNombre());
                    existing.setEmail(usuario.getEmail());
                    existing.setPassword(usuario.getPassword());
                    existing.setRol(usuario.getRol());
                    return usuarioRepository.save(existing);
                }).orElse(null);
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
}
