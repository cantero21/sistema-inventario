package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.ProveedorRequest;
import com.cantero.sistema_inventario.dto.ProveedorResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Proveedor;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import com.cantero.sistema_inventario.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<ProveedorResponse> getAll() {
        return proveedorRepository.findAll().stream()
                .map(ProveedorResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProveedorResponse getById(Long id) {
        return ProveedorResponse.from(obtenerProveedor(id));
    }

    @Transactional
    public ProveedorResponse save(ProveedorRequest request) {
        Proveedor proveedor = new Proveedor();
        aplicar(proveedor, request);
        return ProveedorResponse.from(proveedorRepository.save(proveedor));
    }

    @Transactional
    public ProveedorResponse update(Long id, ProveedorRequest request) {
        Proveedor existing = obtenerProveedor(id);
        aplicar(existing, request);
        return ProveedorResponse.from(proveedorRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!proveedorRepository.existsById(id)) {
            throw noEncontrado(id);
        }
        if (productoRepository.existsByProveedorId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "PROVEEDOR_EN_USO",
                    "El proveedor tiene productos asociados y no se puede eliminar");
        }
        proveedorRepository.deleteById(id);
    }

    private void aplicar(Proveedor proveedor, ProveedorRequest request) {
        proveedor.setNombre(request.nombre());
        proveedor.setDireccion(request.direccion());
        proveedor.setTelefono(request.telefono());
        proveedor.setEmail(request.email());
    }

    private Proveedor obtenerProveedor(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
    }

    private ApiException noEncontrado(Long id) {
        return new ApiException(HttpStatus.NOT_FOUND, "PROVEEDOR_NO_ENCONTRADO",
                "Proveedor no encontrado con id " + id);
    }
}
