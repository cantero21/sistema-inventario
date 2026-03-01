package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.model.Categoria;
import com.cantero.sistema_inventario.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> getAll() {
        return ResponseEntity.ok(categoriaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable Long id) {
        Categoria categoria = categoriaService.getById(id);
        return categoria != null ? ResponseEntity.ok(categoria) : ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<Categoria> create(@Valid @RequestBody Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.save(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable Long id, @Valid @RequestBody Categoria categoria) {
        Categoria updated = categoriaService.update(id, categoria);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}