package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.EstadoFactura;
import com.cantero.sistema_inventario.model.Factura;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record FacturaResponse(
        Long id,
        LocalDate fecha,
        double total,
        EstadoFactura estado,
        LocalDateTime fechaAnulacion,
        String usuarioAnulacion,
        ClienteResponse cliente,
        UsuarioResponse usuario,
        List<FacturaItemResponse> items) {

    public static FacturaResponse from(Factura factura) {
        List<FacturaItemResponse> items = factura.getItems() == null ? List.of()
                : factura.getItems().stream().map(FacturaItemResponse::from).toList();

        return new FacturaResponse(
                factura.getId(),
                factura.getFecha(),
                factura.getTotal(),
                factura.getEstado(),
                factura.getFechaAnulacion(),
                factura.getUsuarioAnulacion() == null ? null : factura.getUsuarioAnulacion().getEmail(),
                ClienteResponse.from(factura.getCliente()),
                UsuarioResponse.from(factura.getUsuario()),
                items);
    }
}
