package com.cantero.sistema_inventario.repository;

import com.cantero.sistema_inventario.dto.StockBajoResponse;
import com.cantero.sistema_inventario.model.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.proveedor LEFT JOIN FETCH p.categoria")
    List<Producto> findAllWithRelations();

    @Query("""
            SELECT p FROM Producto p
            LEFT JOIN FETCH p.proveedor
            LEFT JOIN FETCH p.categoria
            WHERE p.id = :id
            """)
    Optional<Producto> findByIdWithRelations(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT new com.cantero.sistema_inventario.dto.StockBajoResponse(p.nombre, p.stock)
            FROM Producto p
            WHERE p.stock < :umbral
            ORDER BY p.stock ASC, p.nombre ASC
            """)
    List<StockBajoResponse> findStockBajo(@Param("umbral") int umbral);

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByProveedorId(Long proveedorId);
}
