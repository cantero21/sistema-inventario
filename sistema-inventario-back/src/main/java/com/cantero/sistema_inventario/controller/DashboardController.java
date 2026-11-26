package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.dto.StockBajoResponse;
import com.cantero.sistema_inventario.dto.VentaPorDiaResponse;
import com.cantero.sistema_inventario.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/totales")
    public ResponseEntity<Map<String, Long>> getTotales() {
        return ResponseEntity.ok(dashboardService.getTotales());
    }

    @GetMapping("/ventas-por-dia")
    public ResponseEntity<List<VentaPorDiaResponse>> getVentasPorDia() {
        return ResponseEntity.ok(dashboardService.getVentasPorDia());
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<StockBajoResponse>> getStockBajo(
            @RequestParam(defaultValue = "10") int umbral) {
        return ResponseEntity.ok(dashboardService.getStockBajo(umbral));
    }
}
