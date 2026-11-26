package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.StockBajoResponse;
import com.cantero.sistema_inventario.dto.VentaPorDiaResponse;
import com.cantero.sistema_inventario.model.EstadoFactura;
import com.cantero.sistema_inventario.repository.ClienteRepository;
import com.cantero.sistema_inventario.repository.FacturaRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import com.cantero.sistema_inventario.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int DIAS_VENTAS = 30;

    private final ProductoRepository productoRepo;
    private final ClienteRepository clienteRepo;
    private final ProveedorRepository proveedorRepo;
    private final FacturaRepository facturaRepo;

    @Transactional(readOnly = true)
    public Map<String, Long> getTotales() {
        Map<String, Long> totales = new HashMap<>();
        totales.put("producto", productoRepo.count());
        totales.put("cliente", clienteRepo.count());
        totales.put("proveedor", proveedorRepo.count());
        totales.put("factura", facturaRepo.countByEstado(EstadoFactura.ACTIVA));
        return totales;
    }

    @Transactional(readOnly = true)
    public List<VentaPorDiaResponse> getVentasPorDia() {
        LocalDate hasta = LocalDate.now();
        LocalDate desde = hasta.minusDays(DIAS_VENTAS - 1);

        Map<LocalDate, Double> totalesPorDia = new HashMap<>();
        for (VentaPorDiaResponse venta : facturaRepo.ventasPorDia(
                EstadoFactura.ACTIVA, desde, hasta)) {
            totalesPorDia.put(venta.fecha(), venta.total());
        }

        List<VentaPorDiaResponse> resultado = new ArrayList<>(DIAS_VENTAS);
        for (int i = 0; i < DIAS_VENTAS; i++) {
            LocalDate fecha = desde.plusDays(i);
            resultado.add(new VentaPorDiaResponse(
                    fecha,
                    totalesPorDia.getOrDefault(fecha, 0.0)));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<StockBajoResponse> getStockBajo(int umbral) {
        return productoRepo.findStockBajo(umbral);
    }
}
