package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.model.Categoria;
import com.cantero.sistema_inventario.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> getAll() {
        return categoriaRepository.findAll();
    }

    public Categoria getById(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria update(Long id, Categoria categoria) {
        return categoriaRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(categoria.getNombre());
                    existing.setDescripcion(categoria.getDescripcion());
                    return categoriaRepository.save(existing);
                }).orElse(null);
    }

    public void delete(Long id) {
        categoriaRepository.deleteById(id);
    }
}
