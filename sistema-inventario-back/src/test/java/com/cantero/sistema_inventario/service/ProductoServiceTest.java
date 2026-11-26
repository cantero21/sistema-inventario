package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.ProductoRequest;
import com.cantero.sistema_inventario.dto.ProductoResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Producto;
import com.cantero.sistema_inventario.repository.CategoriaRepository;
import com.cantero.sistema_inventario.repository.FacturaItemRepository;
import com.cantero.sistema_inventario.repository.MovimientoRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import com.cantero.sistema_inventario.repository.ProveedorRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    ProductoRepository productoRepository;

    @Mock
    MovimientoRepository movimientoRepository;

    @Mock
    FacturaItemRepository facturaItemRepository;

    @Mock
    CategoriaRepository categoriaRepository;

    @Mock
    ProveedorRepository proveedorRepository;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    ProductoService productoService;

    @Test
    void rechazaCambiarElStockDesdeLaEdicion() {
        Producto existing = Producto.builder()
                .id(1L).nombre("Producto").precio(10.0).stock(5).build();
        ProductoRequest request = new ProductoRequest("Producto", null, 10.0, 9, null, null);

        when(productoRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> productoService.update(1L, request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(api.getCodigo()).isEqualTo("STOCK_NO_EDITABLE");
                });

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void permiteEditarSinCambiarElStock() {
        Producto existing = Producto.builder()
                .id(1L).nombre("Producto").precio(10.0).stock(5).build();
        ProductoRequest request = new ProductoRequest("Producto editado", null, 12.0, 5, null, null);

        when(productoRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(existing));
        when(productoRepository.save(existing)).thenReturn(existing);

        ProductoResponse response = productoService.update(1L, request);

        assertThat(response.nombre()).isEqualTo("Producto editado");
        assertThat(response.precio()).isEqualTo(12.0);
        assertThat(response.stock()).isEqualTo(5);
    }

    @Test
    void noEliminaUnProductoIncluidoEnFacturas() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(facturaItemRepository.existsByProductoId(1L)).thenReturn(true);

        assertThatThrownBy(() -> productoService.delete(1L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(api.getCodigo()).isEqualTo("PRODUCTO_EN_FACTURAS");
                });

        verify(productoRepository, never()).deleteById(any(Long.class));
    }
}
