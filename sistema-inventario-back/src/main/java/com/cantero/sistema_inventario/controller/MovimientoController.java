package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.model.Movimiento;
import com.cantero.sistema_inventario.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;


    @GetMapping
    public ResponseEntity<List<Movimiento>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }


    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Movimiento>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(movimientoService.listarPorProducto(productoId));
    }


    @PostMapping("/entrada")
    public ResponseEntity<Movimiento> crearEntrada(@RequestBody Movimiento body) {
        Movimiento movimiento = movimientoService.crearMovimientoEntrada(
                body.getProducto().getId(),
                body.getCantidad(),
                body.getPrecioUnitario()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }


    @PostMapping("/salida")
    public ResponseEntity<Movimiento> crearSalida(@RequestBody Movimiento body) {
        Movimiento movimiento = movimientoService.crearMovimientoSalida(
                body.getProducto().getId(),
                body.getCantidad(),
                body.getPrecioUnitario(),
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }
}