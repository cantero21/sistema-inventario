package com.cantero.sistema_inventario.repository;

import com.cantero.sistema_inventario.dto.VentaPorDiaResponse;
import com.cantero.sistema_inventario.model.EstadoFactura;
import com.cantero.sistema_inventario.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    @Query("""
            SELECT DISTINCT f FROM Factura f
            LEFT JOIN FETCH f.cliente
            LEFT JOIN FETCH f.items i
            LEFT JOIN FETCH i.producto
            """)
    List<Factura> findAllWithItems();

    @Query("""
            SELECT DISTINCT f FROM Factura f
            LEFT JOIN FETCH f.cliente
            LEFT JOIN FETCH f.items i
            LEFT JOIN FETCH i.producto
            WHERE f.id = :id
            """)
    Optional<Factura> findByIdWithItems(@Param("id") Long id);

    boolean existsByClienteId(Long clienteId);

    long countByEstado(EstadoFactura estado);

    @Query("""
            SELECT new com.cantero.sistema_inventario.dto.VentaPorDiaResponse(f.fecha, SUM(f.total))
            FROM Factura f
            WHERE f.estado = :estado
              AND f.fecha BETWEEN :desde AND :hasta
            GROUP BY f.fecha
            ORDER BY f.fecha
            """)
    List<VentaPorDiaResponse> ventasPorDia(
            @Param("estado") EstadoFactura estado,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);
}
