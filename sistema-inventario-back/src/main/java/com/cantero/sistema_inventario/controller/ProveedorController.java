package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.model.Proveedor;
import com.cantero.sistema_inventario.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<Proveedor>> getAll() {
        return ResponseEntity.ok(proveedorService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getById(@PathVariable Long id) {
        Proveedor proveedor = proveedorService.getById(id);
        return proveedor != null ? ResponseEntity.ok(proveedor) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Proveedor> create(@Valid @RequestBody Proveedor proveedor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.save(proveedor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> update(@PathVariable Long id, @Valid @RequestBody Proveedor proveedor) {
        Proveedor updated = proveedorService.update(id, proveedor);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proveedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}