package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.ClienteRequest;
import com.cantero.sistema_inventario.dto.ClienteResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Cliente;
import com.cantero.sistema_inventario.repository.ClienteRepository;
import com.cantero.sistema_inventario.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final FacturaRepository facturaRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponse> getAll() {
        return clienteRepository.findAll().stream()
                .map(ClienteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse getById(Long id) {
        return ClienteResponse.from(obtenerCliente(id));
    }

    @Transactional
    public ClienteResponse save(ClienteRequest request) {
        Cliente cliente = new Cliente();
        aplicar(cliente, request);
        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponse update(Long id, ClienteRequest request) {
        Cliente existing = obtenerCliente(id);
        aplicar(existing, request);
        return ClienteResponse.from(clienteRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw noEncontrado(id);
        }
        if (facturaRepository.existsByClienteId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CLIENTE_CON_FACTURAS",
                    "El cliente tiene facturas emitidas y no se puede eliminar");
        }
        clienteRepository.deleteById(id);
    }

    private void aplicar(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.nombre());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        cliente.setEmail(request.email());
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
    }

    private ApiException noEncontrado(Long id) {
        return new ApiException(HttpStatus.NOT_FOUND, "CLIENTE_NO_ENCONTRADO",
                "Cliente no encontrado con id " + id);
    }
}
