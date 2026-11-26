package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Producto;
import com.cantero.sistema_inventario.repository.MovimientoRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    MovimientoRepository movimientoRepository;

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    MovimientoService movimientoService;

    @Test
    void rechazaSalidaSinStockSuficiente() {
        Producto producto = Producto.builder().id(1L).nombre("Café").stock(2).build();
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> movimientoService.crearMovimientoSalida(1L, 5, 10.0, null))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(api.getCodigo()).isEqualTo("STOCK_INSUFICIENTE");
                });

        verify(productoRepository).findByIdForUpdate(1L);
    }

    @Test
    void rechazaCantidadNoPositiva() {
        assertThatThrownBy(() -> movimientoService.crearMovimientoEntrada(1L, 0, 10.0))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(api.getCodigo()).isEqualTo("CANTIDAD_INVALIDA");
                });
    }
}
