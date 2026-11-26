package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.dto.ProveedorRequest;
import com.cantero.sistema_inventario.dto.ProveedorResponse;
import com.cantero.sistema_inventario.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorResponse>> getAll() {
        return ResponseEntity.ok(proveedorService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProveedorResponse> create(@Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proveedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
