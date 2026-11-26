package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.dto.MovimientoRequest;
import com.cantero.sistema_inventario.dto.MovimientoResponse;
import com.cantero.sistema_inventario.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MovimientoResponse>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(movimientoService.listarPorProducto(productoId));
    }

    @PostMapping("/entrada")
    public ResponseEntity<MovimientoResponse> crearEntrada(@Valid @RequestBody MovimientoRequest request) {
        MovimientoResponse movimiento = movimientoService.crearMovimientoEntrada(
                request.productoId(),
                request.cantidad(),
                request.precioUnitario()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }

    @PostMapping("/salida")
    public ResponseEntity<MovimientoResponse> crearSalida(@Valid @RequestBody MovimientoRequest request) {
        MovimientoResponse movimiento = movimientoService.crearMovimientoSalida(
                request.productoId(),
                request.cantidad(),
                request.precioUnitario(),
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }
}
