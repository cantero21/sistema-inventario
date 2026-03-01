package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.model.Factura;
import com.cantero.sistema_inventario.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping
    public ResponseEntity<List<Factura>> getAll() {
        return ResponseEntity.ok(facturaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> getById(@PathVariable Long id) {
        Factura factura = facturaService.getByIdConDetalles(id);
        return factura != null ? ResponseEntity.ok(factura) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Factura> create(@RequestBody Factura factura) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaService.save(factura));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Factura> update(@PathVariable Long id, @RequestBody Factura factura) {
        Factura updated = facturaService.update(id, factura);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
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