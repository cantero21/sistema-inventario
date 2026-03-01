package com.cantero.sistema_inventario.repository;

import com.cantero.sistema_inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p JOIN FETCH p.proveedor JOIN FETCH p.categoria")
    List<Producto> findAllWithRelations();
}
