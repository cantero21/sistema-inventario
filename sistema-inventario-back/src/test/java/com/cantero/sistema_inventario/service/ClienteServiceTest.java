package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.repository.ClienteRepository;
import com.cantero.sistema_inventario.repository.FacturaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    FacturaRepository facturaRepository;

    @InjectMocks
    ClienteService clienteService;

    @Test
    void noEliminaUnClienteConFacturas() {
        when(clienteRepository.existsById(4L)).thenReturn(true);
        when(facturaRepository.existsByClienteId(4L)).thenReturn(true);

        assertThatThrownBy(() -> clienteService.delete(4L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(api.getCodigo()).isEqualTo("CLIENTE_CON_FACTURAS");
                });

        verify(clienteRepository, never()).deleteById(any(Long.class));
    }
}
