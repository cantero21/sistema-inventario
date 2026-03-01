package com.cantero.sistema_inventario.security.jwt;

import com.cantero.sistema_inventario.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        String email = null;
        String rol = null;


        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                email = jwtTokenProvider.obtenerEmailDelToken(token);
                rol = jwtTokenProvider.obtenerRolDelToken(token); // ✅ extraemos el rol
            } catch (Exception e) {
                System.out.println("❌ Token inválido o expirado: " + e.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);


            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            Collections.singletonList(authority)
                    );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
