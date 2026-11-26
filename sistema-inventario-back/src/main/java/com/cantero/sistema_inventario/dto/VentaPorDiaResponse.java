package com.cantero.sistema_inventario.dto;

import java.time.LocalDate;

public record VentaPorDiaResponse(LocalDate fecha, Double total) {
}
