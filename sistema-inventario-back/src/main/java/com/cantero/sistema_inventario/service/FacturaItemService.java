package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.FacturaItemResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.FacturaItem;
import com.cantero.sistema_inventario.repository.FacturaItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Solo lectura: los ítems forman parte de una factura emitida y no deben
 * modificarse por separado, porque alterarían el stock sin movimiento asociado.
 */
@Service
@RequiredArgsConstructor
public class FacturaItemService {

    private final FacturaItemRepository facturaItemRepository;

    @Transactional(readOnly = true)
    public List<FacturaItemResponse> getAll() {
        return facturaItemRepository.findAll().stream()
                .map(FacturaItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FacturaItemResponse getById(Long id) {
        return FacturaItemResponse.from(obtenerItem(id));
    }

    private FacturaItem obtenerItem(Long id) {
        return facturaItemRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "FACTURA_ITEM_NO_ENCONTRADO",
                        "Ítem de factura no encontrado con id " + id));
    }
}
