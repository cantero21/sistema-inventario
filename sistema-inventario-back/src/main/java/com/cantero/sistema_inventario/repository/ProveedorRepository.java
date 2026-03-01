package com.cantero.sistema_inventario.repository;

import com.cantero.sistema_inventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}

