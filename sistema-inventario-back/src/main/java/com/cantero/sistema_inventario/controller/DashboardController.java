package com.cantero.sistema_inventario.controller;

import com.cantero.sistema_inventario.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;


    @GetMapping("/totales")
    public ResponseEntity<Map<String, Long>> getTotales() {
        return ResponseEntity.ok(dashboardService.getTotales());
    }
}