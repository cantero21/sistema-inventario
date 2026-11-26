package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.dto.FacturaItemResponse;
import com.cantero.sistema_inventario.service.FacturaItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factura-items")
@RequiredArgsConstructor
public class FacturaItemController {

    private final FacturaItemService facturaItemService;

    @GetMapping
    public ResponseEntity<List<FacturaItemResponse>> getAll() {
        return ResponseEntity.ok(facturaItemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaItemResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facturaItemService.getById(id));
    }
}
