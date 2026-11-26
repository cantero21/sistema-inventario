package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.repository.CategoriaRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
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
class CategoriaServiceTest {

    @Mock
    CategoriaRepository categoriaRepository;

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    CategoriaService categoriaService;

    @Test
    void noEliminaUnaCategoriaConProductos() {
        when(categoriaRepository.existsById(3L)).thenReturn(true);
        when(productoRepository.existsByCategoriaId(3L)).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.delete(3L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(api.getCodigo()).isEqualTo("CATEGORIA_EN_USO");
                });

        verify(categoriaRepository, never()).deleteById(any(Long.class));
    }
}
