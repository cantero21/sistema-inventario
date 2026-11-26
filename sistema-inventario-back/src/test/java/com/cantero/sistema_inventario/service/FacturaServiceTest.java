package com.cantero.sistema_inventario.service;

import com.cantero.sistema_inventario.dto.FacturaItemRequest;
import com.cantero.sistema_inventario.dto.FacturaRequest;
import com.cantero.sistema_inventario.dto.FacturaResponse;
import com.cantero.sistema_inventario.exception.ApiException;
import com.cantero.sistema_inventario.model.Cliente;
import com.cantero.sistema_inventario.model.EstadoFactura;
import com.cantero.sistema_inventario.model.Factura;
import com.cantero.sistema_inventario.model.FacturaItem;
import com.cantero.sistema_inventario.model.Movimiento;
import com.cantero.sistema_inventario.model.Producto;
import com.cantero.sistema_inventario.model.TipoMovimiento;
import com.cantero.sistema_inventario.model.Usuario;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cantero.sistema_inventario.repository.ClienteRepository;
import com.cantero.sistema_inventario.repository.FacturaRepository;
import com.cantero.sistema_inventario.repository.ProductoRepository;
import com.cantero.sistema_inventario.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    FacturaRepository facturaRepository;

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    ProductoRepository productoRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    MovimientoService movimientoService;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    FacturaService facturaService;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void creaFacturaConPrecioYTotalDelProducto() {
        autenticar();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(productoRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(producto(10)));
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario()));

        AtomicReference<Factura> guardada = capturarFacturaGuardada();

        FacturaRequest request = new FacturaRequest(
                LocalDate.of(2026, 11, 16), 1L,
                List.of(new FacturaItemRequest(5L, 2)));

        FacturaResponse response = facturaService.save(request);

        assertThat(response.total()).isEqualTo(20.0);
        assertThat(response.items().get(0).precioUnitario()).isEqualTo(10.0);
        assertThat(response.cliente().nombre()).isEqualTo("Ana");
        assertThat(response.usuario().email()).isEqualTo("admin@test.com");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).producto().nombre()).isEqualTo("Café");
        verify(movimientoService).crearMovimientoSalida(5L, 2, 10.0, guardada.get());
    }

    @Test
    void procesaItemsEnOrdenDeProductoIdSinCambiarElOrdenGuardado() {
        autenticar();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario()));
        when(productoRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(
                Producto.builder().id(7L).nombre("Té").precio(5.0).stock(10).build()));
        when(productoRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(
                Producto.builder().id(3L).nombre("Café").precio(10.0).stock(10).build()));

        AtomicReference<Factura> guardada = capturarFacturaGuardada();

        FacturaRequest request = new FacturaRequest(
                LocalDate.of(2026, 11, 16), 1L,
                List.of(new FacturaItemRequest(7L, 2), new FacturaItemRequest(3L, 1)));

        FacturaResponse response = facturaService.save(request);

        assertThat(response.total()).isEqualTo(20.0);
        assertThat(response.items())
                .extracting(item -> item.producto().id())
                .containsExactly(7L, 3L);

        InOrder orden = inOrder(movimientoService);
        orden.verify(movimientoService).crearMovimientoSalida(3L, 1, 10.0, guardada.get());
        orden.verify(movimientoService).crearMovimientoSalida(7L, 2, 5.0, guardada.get());
    }

    @Test
    void usaElPrecioDelProductoAunqueElRequestTraigaOtroPrecio() throws Exception {
        autenticar();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(productoRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(producto(10)));
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario()));

        AtomicReference<Factura> guardada = capturarFacturaGuardada();

        String json = """
                {"clienteId":1,
                 "items":[{"productoId":5,"cantidad":2,"precioUnitario":0.01}]}
                """;
        ObjectMapper mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        FacturaResponse response = facturaService.save(mapper.readValue(json, FacturaRequest.class));

        assertThat(response.total()).isEqualTo(20.0);
        assertThat(response.items().get(0).precioUnitario()).isEqualTo(10.0);
        verify(movimientoService).crearMovimientoSalida(5L, 2, 10.0, guardada.get());
    }

    @Test
    void rechazaProductoDuplicadoEnLosItems() {
        FacturaRequest request = new FacturaRequest(
                LocalDate.of(2026, 11, 16), 1L,
                List.of(new FacturaItemRequest(5L, 2), new FacturaItemRequest(5L, 1)));

        assertThatThrownBy(() -> facturaService.save(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(api.getCodigo()).isEqualTo("PRODUCTO_DUPLICADO");
                });

        verify(facturaRepository, never()).save(any(Factura.class));
    }

    @Test
    void rechazaProductoConPrecioNulo() {
        autenticar();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario()));
        when(productoRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(
                Producto.builder().id(5L).nombre("Café").precio(null).stock(10).build()));

        FacturaRequest request = new FacturaRequest(
                LocalDate.of(2026, 11, 16), 1L,
                List.of(new FacturaItemRequest(5L, 1)));

        assertThatThrownBy(() -> facturaService.save(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(api.getCodigo()).isEqualTo("PRODUCTO_SIN_PRECIO");
                });

        verify(facturaRepository, never()).save(any(Factura.class));
    }

    @Test
    void rechazaProductoInexistenteSinGuardar() {
        autenticar();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario()));
        when(productoRepository.findByIdForUpdate(5L)).thenReturn(Optional.empty());

        FacturaRequest request = new FacturaRequest(
                LocalDate.of(2026, 11, 16), 1L,
                List.of(new FacturaItemRequest(5L, 2)));

        assertThatThrownBy(() -> facturaService.save(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(api.getCodigo()).isEqualTo("PRODUCTO_NO_ENCONTRADO");
                });
    }

    @Test
    void anulaFacturaDevuelveStockYRegistraAuditoriaSinBorrarla() {
        autenticar();
        Factura factura = facturaActiva();
        int movimientosOriginales = factura.getMovimientos().size();

        when(facturaRepository.findByIdWithItems(7L)).thenReturn(Optional.of(factura));
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario()));

        facturaService.anularFactura(7L);

        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.ANULADA);
        assertThat(factura.getFechaAnulacion()).isNotNull();
        assertThat(factura.getUsuarioAnulacion().getEmail()).isEqualTo("admin@test.com");
        assertThat(factura.getMovimientos()).hasSize(movimientosOriginales);

        verify(movimientoService).crearMovimientoEntrada(5L, 2, 10.0);
        verify(facturaRepository).save(factura);
        verify(facturaRepository, never()).delete(any(Factura.class));
    }

    @Test
    void rechazaAnularFacturaYaAnulada() {
        Factura factura = facturaActiva();
        factura.setEstado(EstadoFactura.ANULADA);
        when(facturaRepository.findByIdWithItems(7L)).thenReturn(Optional.of(factura));

        assertThatThrownBy(() -> facturaService.anularFactura(7L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(api.getCodigo()).isEqualTo("FACTURA_YA_ANULADA");
                });

        verifyNoInteractions(movimientoService);
        verify(facturaRepository, never()).save(any(Factura.class));
        verify(facturaRepository, never()).delete(any(Factura.class));
    }

    @Test
    void deleteNoAnulaNiDevuelveStock() {
        assertThatThrownBy(() -> facturaService.delete(7L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException api = (ApiException) ex;
                    assertThat(api.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(api.getCodigo()).isEqualTo("FACTURA_NO_ELIMINABLE");
                });

        verifyNoInteractions(movimientoService);
        verifyNoInteractions(facturaRepository);
    }

    private void autenticar() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@test.com", null, List.of()));
    }

    private AtomicReference<Factura> capturarFacturaGuardada() {
        AtomicReference<Factura> guardada = new AtomicReference<>();
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> {
            Factura factura = invocation.getArgument(0);
            factura.setId(10L);
            guardada.set(factura);
            return factura;
        });
        when(facturaRepository.findByIdWithItems(10L))
                .thenAnswer(invocation -> Optional.ofNullable(guardada.get()));
        return guardada;
    }

    private Cliente cliente() {
        return Cliente.builder().id(1L).nombre("Ana").build();
    }

    private Factura facturaActiva() {
        FacturaItem item = FacturaItem.builder()
                .id(9L)
                .cantidad(2)
                .precioUnitario(10.0)
                .producto(producto(10))
                .build();

        Factura factura = Factura.builder()
                .id(7L)
                .fecha(LocalDate.of(2026, 11, 16))
                .total(20.0)
                .cliente(cliente())
                .usuario(usuario())
                .items(new ArrayList<>(List.of(item)))
                .movimientos(new ArrayList<>(List.of(Movimiento.builder()
                        .id(1L)
                        .fecha(LocalDateTime.of(2026, 11, 16, 9, 0))
                        .tipo(TipoMovimiento.SALIDA)
                        .cantidad(2)
                        .precioUnitario(10.0)
                        .producto(producto(10))
                        .build())))
                .build();

        item.setFactura(factura);
        return factura;
    }

    private Producto producto(int stock) {
        return Producto.builder().id(5L).nombre("Café").precio(10.0).stock(stock).build();
    }

    private Usuario usuario() {
        return Usuario.builder().id(2L).nombre("Admin").email("admin@test.com").rol("ADMIN").build();
    }
}
