package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.ProductoRequest;
import com.cantero.sistema_inventario.dto.ProductoResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Categoria;
import com.cantero.sistema_inventario.model.Producto;
import com.cantero.sistema_inventario.model.Proveedor;
import com.cantero.sistema_inventario.repository.CategoriaRepository;
import com.cantero.sistema_inventario.repository.FacturaItemRepository;
import com.cantero.sistema_inventario.repository.MovimientoRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import com.cantero.sistema_inventario.repository.ProveedorRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final FacturaItemRepository facturaItemRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<ProductoResponse> getAllProductos() {
        return productoRepository.findAllWithRelations().stream()
                .map(ProductoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse getById(Long id) {
        return ProductoResponse.from(obtenerProducto(id));
    }

    @Transactional
    public ProductoResponse save(ProductoRequest request) {
        Producto producto = new Producto();
        aplicar(producto, request, true);
        return recargar(productoRepository.save(producto).getId());
    }

    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto existing = obtenerProducto(id);

        // El stock solo cambia mediante movimientos de inventario o facturas.
        if (request.stock() != null && !request.stock().equals(existing.getStock())) {
            throw new ApiException(HttpStatus.CONFLICT, "STOCK_NO_EDITABLE",
                    "El stock no se modifica desde la edición del producto; "
                            + "usa un movimiento de entrada o salida");
        }

        aplicar(existing, request, false);
        productoRepository.save(existing);

        return recargar(id);
    }

    @Transactional
    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw noEncontrado(id);
        }
        if (facturaItemRepository.existsByProductoId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "PRODUCTO_EN_FACTURAS",
                    "El producto está incluido en facturas emitidas y no se puede eliminar");
        }
        movimientoRepository.deleteByProductoId(id);
        productoRepository.deleteById(id);
    }

    private void aplicar(Producto producto, ProductoRequest request, boolean esNuevo) {
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        if (esNuevo) {
            producto.setStock(request.stock() == null ? 0 : request.stock());
        }
        producto.setCategoria(resolverCategoria(request.categoriaId()));
        producto.setProveedor(resolverProveedor(request.proveedorId()));
    }

    private Categoria resolverCategoria(Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "CATEGORIA_NO_ENCONTRADA", "Categoría no encontrada con id " + categoriaId));
    }

    private Proveedor resolverProveedor(Long proveedorId) {
        if (proveedorId == null) {
            return null;
        }
        return proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "PROVEEDOR_NO_ENCONTRADO", "Proveedor no encontrado con id " + proveedorId));
    }

    private ProductoResponse recargar(Long id) {
        productoRepository.flush();
        entityManager.clear();
        return ProductoResponse.from(obtenerProducto(id));
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> noEncontrado(id));
    }

    private ApiException noEncontrado(Long id) {
        return new ApiException(HttpStatus.NOT_FOUND, "PRODUCTO_NO_ENCONTRADO",
                "Producto no encontrado con id " + id);
    }
}
