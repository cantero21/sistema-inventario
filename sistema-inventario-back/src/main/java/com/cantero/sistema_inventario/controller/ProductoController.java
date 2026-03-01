package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.model.Producto;
import com.cantero.sistema_inventario.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getAll() {
        return ResponseEntity.ok(productoService.getAllProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        Producto producto = productoService.getById(id);
        return producto != null ? ResponseEntity.ok(producto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Producto> create(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.save(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        Producto updated = productoService.update(id, producto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}