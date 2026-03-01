package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.model.FacturaItem;
import com.cantero.sistema_inventario.service.FacturaItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factura-items")
public class FacturaItemController {

    @Autowired
    private FacturaItemService facturaItemService;

    @GetMapping
    public ResponseEntity<List<FacturaItem>> getAll() {
        return ResponseEntity.ok(facturaItemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaItem> getById(@PathVariable Long id) {
        FacturaItem item = facturaItemService.getById(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<FacturaItem> create(@Valid @RequestBody FacturaItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaItemService.save(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaItem> update(@PathVariable Long id, @Valid @RequestBody FacturaItem item) {
        FacturaItem updated = facturaItemService.update(id, item);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facturaItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}