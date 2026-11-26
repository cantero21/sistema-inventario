package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.dto.FacturaRequest;
import com.cantero.sistema_inventario.dto.FacturaResponse;
import com.cantero.sistema_inventario.service.FacturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    @GetMapping
    public ResponseEntity<List<FacturaResponse>> getAll() {
        return ResponseEntity.ok(facturaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<FacturaResponse> create(@Valid @RequestBody FacturaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody FacturaRequest request) {
        return ResponseEntity.ok(facturaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facturaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/anular/{id}")
    public ResponseEntity<Map<String, String>> anularFactura(@PathVariable Long id) {
        facturaService.anularFactura(id);
        return ResponseEntity.ok(Map.of("mensaje", "Factura anulada con éxito y stock devuelto"));
    }
}
