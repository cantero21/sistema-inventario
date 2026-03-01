package com.cantero.sistema_inventario.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {


    private final String SECRET_KEY = "MiClaveSecretaMuySeguraParaFirmarElToken123456789";


    private final long EXPIRATION_TIME = 86400000;


    public String generarToken(String email, String rol) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_TIME);


        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public String obtenerEmailDelToken(String token) {
        return getAllClaims(token).getSubject();
    }


    public String obtenerRolDelToken(String token) {
        return (String) getAllClaims(token).get("rol");
    }


    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
