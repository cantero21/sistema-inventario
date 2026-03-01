package com.cantero.sistema_inventario.security.config;

import com.cantero.sistema_inventario.security.jwt.JwtAuthenticationFilter;
import com.cantero.sistema_inventario.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;


@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/productos/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/categorias/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/clientes/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/proveedores/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/proveedores/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/facturas/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/facturas/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/movimientos/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/movimientos/**").hasRole("ADMIN")


                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // 🔒 Cualquier otro endpoint requiere autenticación
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
