package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.model.Producto;
import com.cantero.sistema_inventario.repository.MovimientoRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private MovimientoRepository movimientoRepository;

    public List<Producto> getAllProductos() {
        return productoRepository.findAllWithRelations();
    }

    public Producto getById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto update(Long id, Producto producto) {
        return productoRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(producto.getNombre());
                    existing.setDescripcion(producto.getDescripcion());
                    existing.setPrecio(producto.getPrecio());
                    existing.setStock(producto.getStock());
                    existing.setCategoria(producto.getCategoria());
                    existing.setProveedor(producto.getProveedor());
                    return productoRepository.save(existing);
                })
                .orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        movimientoRepository.deleteByProductoId(id);
        productoRepository.deleteById(id);
    }
}