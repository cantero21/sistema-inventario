package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.model.Factura;
import com.cantero.sistema_inventario.model.FacturaItem;
import com.cantero.sistema_inventario.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private MovimientoService movimientoService;

    public List<Factura> getAll() {
        return facturaRepository.findAll();
    }

    public Factura getById(Long id) {
        return facturaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Factura save(Factura factura) {
        // Calcular total
        if (factura.getItems() != null) {
            double total = factura.getItems().stream()
                    .mapToDouble(item -> item.getCantidad() * item.getPrecioUnitario())
                    .sum();
            factura.setTotal(total);
        }


        Factura savedFactura = facturaRepository.save(factura);


        if (savedFactura.getItems() != null) {
            savedFactura.getItems().forEach(item -> {
                movimientoService.crearMovimientoSalida(
                        item.getProducto().getId(),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        savedFactura
                );
            });
        }

        return savedFactura;
    }

    public Factura update(Long id, Factura factura) {
        return facturaRepository.findById(id)
                .map(existing -> {
                    existing.setFecha(factura.getFecha());
                    existing.setCliente(factura.getCliente());
                    existing.setItems(factura.getItems());

                    if (factura.getItems() != null) {
                        Double total = factura.getItems().stream()
                                .mapToDouble(item -> item.getCantidad() * item.getPrecioUnitario())
                                .sum();
                        existing.setTotal(total);
                    }

                    return facturaRepository.save(existing);
                }).orElse(null);
    }

    @Transactional(readOnly = true)
    public Factura getByIdConDetalles(Long id) {
        return facturaRepository.findById(id).map(factura -> {
            factura.getItems().size();
            factura.getMovimientos().size();
            if (factura.getCliente() != null) factura.getCliente().getNombre();
            if (factura.getUsuario() != null) factura.getUsuario().getNombre();
            return factura;
        }).orElse(null);
    }


    @Transactional
    public void anularFactura(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (factura.getItems() != null) {
            factura.getItems().forEach(item -> {
                movimientoService.crearMovimientoEntrada(
                        item.getProducto().getId(),
                        item.getCantidad(),
                        item.getPrecioUnitario()
                );
            });
        }


        if (factura.getMovimientos() != null) {
            factura.getMovimientos().forEach(mov -> mov.setFactura(null));
            factura.getMovimientos().clear();
        }

        facturaRepository.delete(factura);
    }

    public void delete(Long id) {
        facturaRepository.deleteById(id);
    }
}
