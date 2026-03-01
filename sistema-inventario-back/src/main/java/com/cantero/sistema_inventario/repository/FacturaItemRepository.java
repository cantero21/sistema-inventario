package com.cantero.sistema_inventario.repository;

import com.cantero.sistema_inventario.model.FacturaItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaItemRepository extends JpaRepository<FacturaItem, Long> {}
