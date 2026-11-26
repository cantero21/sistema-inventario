package com.cantero.sistema_inventario.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FacturaJsonTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void conservaLosItemsAlDeserializarYSerializar() throws Exception {
        String json = """
                {"fecha":"2026-01-15","cliente":{"id":1},
                 "items":[{"cantidad":2,"precioUnitario":10.0,"producto":{"id":5}}]}
                """;

        Factura factura = mapper.readValue(json, Factura.class);

        assertThat(factura.getItems()).hasSize(1);
        assertThat(mapper.writeValueAsString(factura)).contains("\"items\"");
    }
}
