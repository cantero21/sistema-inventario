package com.cantero.sistema_inventario.repository;

import com.cantero.sistema_inventario.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {}


