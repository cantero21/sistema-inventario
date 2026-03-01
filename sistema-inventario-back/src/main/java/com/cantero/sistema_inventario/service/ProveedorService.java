package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.model.Proveedor;
import com.cantero.sistema_inventario.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Proveedor> getAll() {
        return proveedorRepository.findAll();
    }

    public Proveedor getById(Long id) {
        return proveedorRepository.findById(id).orElse(null);
    }

    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public Proveedor update(Long id, Proveedor proveedor) {
        return proveedorRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(proveedor.getNombre());
                    existing.setDireccion(proveedor.getDireccion());
                    existing.setTelefono(proveedor.getTelefono());
                    existing.setEmail(proveedor.getEmail());
                    return proveedorRepository.save(existing);
                }).orElse(null);
    }

    public void delete(Long id) {
        proveedorRepository.deleteById(id);
    }
}
