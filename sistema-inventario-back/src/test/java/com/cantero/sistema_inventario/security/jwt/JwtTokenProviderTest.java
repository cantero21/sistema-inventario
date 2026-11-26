package com.cantero.sistema_inventario.security.jwt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET = "clave-de-prueba-con-mas-de-32-bytes-123456";

    @Test
    void generaYLeeElEmail() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000);
        String token = provider.generarToken("ana@test.com");
        assertThat(provider.obtenerEmailDelToken(token)).isEqualTo("ana@test.com");
    }

    @Test
    void rechazaSecretosDeMenosDe32Bytes() {
        assertThatThrownBy(() -> new JwtTokenProvider("corto", 60_000))
                .isInstanceOf(IllegalStateException.class);
    }
}
