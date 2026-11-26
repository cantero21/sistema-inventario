package com.cantero.sistema_inventario.security.config;

import com.cantero.sistema_inventario.security.jwt.JwtAuthenticationFilter;
import com.cantero.sistema_inventario.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/productos/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/categorias/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/clientes/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/proveedores/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/proveedores/**").hasRole("ADMIN")


                        // Facturación: ambos roles pueden consultar y emitir facturas.
                        // Anular (POST /anular/{id}), editar y borrar quedan solo para ADMIN.
                        .requestMatchers(HttpMethod.GET, "/api/facturas/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/facturas").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/facturas/**").hasRole("ADMIN")


                        // Ítems de factura: solo ADMIN (el frontend no los consume).
                        .requestMatchers("/api/factura-items/**").hasRole("ADMIN")

                        // Dashboard: resumen para cualquier usuario autenticado.
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN")


                        // Inventario: ambos roles pueden registrar salidas (venta).
                        // Las entradas y el resto de operaciones quedan solo para ADMIN.
                        .requestMatchers(HttpMethod.GET, "/api/movimientos/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/movimientos/salida").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/movimientos/**").hasRole("ADMIN")


                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // 🔒 Cualquier otro endpoint requiere autenticación
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
