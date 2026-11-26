package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.CategoriaRequest;
import com.cantero.sistema_inventario.dto.CategoriaResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Categoria;
import com.cantero.sistema_inventario.repository.CategoriaRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> getAll() {
        return categoriaRepository.findAll().stream()
                .map(CategoriaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse getById(Long id) {
        return CategoriaResponse.from(obtenerCategoria(id));
    }

    @Transactional
    public CategoriaResponse save(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        aplicar(categoria, request);
        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse update(Long id, CategoriaRequest request) {
        Categoria existing = obtenerCategoria(id);
        aplicar(existing, request);
        return CategoriaResponse.from(categoriaRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw noEncontrado(id);
        }
        if (productoRepository.existsByCategoriaId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CATEGORIA_EN_USO",
                    "La categoría tiene productos asociados y no se puede eliminar");
        }
        categoriaRepository.deleteById(id);
    }

    private void aplicar(Categoria categoria, CategoriaRequest request) {
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
    }

    private Categoria obtenerCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
    }

    private ApiException noEncontrado(Long id) {
        return new ApiException(HttpStatus.NOT_FOUND, "CATEGORIA_NO_ENCONTRADA",
                "Categoría no encontrada con id " + id);
    }
}
