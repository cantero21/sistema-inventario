package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.model.Cliente;
import com.cantero.sistema_inventario.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> getAll() {
        return clienteRepository.findAll();
    }

    public Cliente getById(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente cliente) {
        return clienteRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(cliente.getNombre());
                    existing.setDireccion(cliente.getDireccion());
                    existing.setTelefono(cliente.getTelefono());
                    existing.setEmail(cliente.getEmail());
                    return clienteRepository.save(existing);
                }).orElse(null);
    }

    public void delete(Long id) {
        clienteRepository.deleteById(id);
    }
}
