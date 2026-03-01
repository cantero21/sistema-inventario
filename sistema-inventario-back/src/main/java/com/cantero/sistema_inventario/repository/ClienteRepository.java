package com.cantero.sistema_inventario.repository;

import com.cantero.sistema_inventario.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}

