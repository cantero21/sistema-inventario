package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.model.FacturaItem;
import com.cantero.sistema_inventario.repository.FacturaItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaItemService {

    @Autowired
    private FacturaItemRepository facturaItemRepository;

    public List<FacturaItem> getAll() {
        return facturaItemRepository.findAll();
    }

    public FacturaItem getById(Long id) {
        return facturaItemRepository.findById(id).orElse(null);
    }

    public FacturaItem save(FacturaItem item) {
        return facturaItemRepository.save(item);
    }

    public FacturaItem update(Long id, FacturaItem item) {
        return facturaItemRepository.findById(id)
                .map(existing -> {
                    existing.setProducto(item.getProducto());
                    existing.setCantidad(item.getCantidad());
                    existing.setPrecioUnitario(item.getPrecioUnitario());
                    existing.setFactura(item.getFactura());
                    return facturaItemRepository.save(existing);
                }).orElse(null);
    }

    public void delete(Long id) {
        facturaItemRepository.deleteById(id);
    }
}
