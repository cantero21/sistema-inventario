package com.cantero.sistema_inventario.repository;

import com.cantero.sistema_inventario.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByProductoIdOrderByFechaDesc(Long productoId);
    List<Movimiento> findAllByOrderByFechaDesc();
    void deleteByProductoId(Long productoId);
}
