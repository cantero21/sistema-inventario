package com.cantero.sistema_inventario.dto;

import com.cantero.sistema_inventario.model.Cliente;
import com.cantero.sistema_inventario.model.EstadoFactura;
import com.cantero.sistema_inventario.model.Factura;
import com.cantero.sistema_inventario.model.FacturaItem;
import com.cantero.sistema_inventario.model.Producto;
import com.cantero.sistema_inventario.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FacturaResponseTest {

    @Test
    void incluyeClienteUsuarioEItemsConProducto() {
        Producto producto = Producto.builder()
                .id(5L).nombre("Café").precio(10.0).stock(3).build();
        FacturaItem item = FacturaItem.builder()
                .id(9L).cantidad(2).precioUnitario(10.0).producto(producto).build();
        Cliente cliente = Cliente.builder().id(1L).nombre("Ana").build();
        Usuario usuario = Usuario.builder().id(2L).nombre("Admin").email("admin@test.com").rol("ADMIN").build();
        Factura factura = Factura.builder()
                .id(7L)
                .fecha(LocalDate.of(2026, 11, 16))
                .total(20.0)
                .cliente(cliente)
                .usuario(usuario)
                .items(List.of(item))
                .build();

        FacturaResponse response = FacturaResponse.from(factura);

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.estado()).isEqualTo(EstadoFactura.ACTIVA);
        assertThat(response.fechaAnulacion()).isNull();
        assertThat(response.usuarioAnulacion()).isNull();
        assertThat(response.cliente().nombre()).isEqualTo("Ana");
        assertThat(response.usuario().email()).isEqualTo("admin@test.com");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).producto().nombre()).isEqualTo("Café");
    }

    @Test
    void exponeEstadoYAuditoriaDeAnulacion() {
        Cliente cliente = Cliente.builder().id(1L).nombre("Ana").build();
        Usuario usuario = Usuario.builder().id(2L).nombre("Admin").email("admin@test.com").rol("ADMIN").build();
        Usuario anulador = Usuario.builder().id(3L).nombre("Super").email("super@test.com").rol("ADMIN").build();
        LocalDateTime fechaAnulacion = LocalDateTime.of(2026, 11, 19, 10, 30);

        Factura factura = Factura.builder()
                .id(7L)
                .fecha(LocalDate.of(2026, 11, 16))
                .total(20.0)
                .estado(EstadoFactura.ANULADA)
                .fechaAnulacion(fechaAnulacion)
                .usuarioAnulacion(anulador)
                .cliente(cliente)
                .usuario(usuario)
                .items(List.of())
                .build();

        FacturaResponse response = FacturaResponse.from(factura);

        assertThat(response.estado()).isEqualTo(EstadoFactura.ANULADA);
        assertThat(response.fechaAnulacion()).isEqualTo(fechaAnulacion);
        assertThat(response.usuarioAnulacion()).isEqualTo("super@test.com");
    }
}
