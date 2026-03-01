package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.repository.ClienteRepository;
import com.cantero.sistema_inventario.repository.FacturaRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import com.cantero.sistema_inventario.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private ProveedorRepository proveedorRepo;

    @Autowired
    private FacturaRepository facturaRepo;


    public Map<String, Long> getTotales() {
        Map<String, Long> totales = new HashMap<>();
        totales.put("producto", productoRepo.count());
        totales.put("cliente", clienteRepo.count());
        totales.put("proveedor", proveedorRepo.count());
        totales.put("factura", facturaRepo.count());
        return totales;
    }
}