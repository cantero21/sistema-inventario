package com.cantero.sistema_inventario.config;

import com.cantero.sistema_inventario.model.Usuario;
import com.cantero.sistema_inventario.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${app.admin.email:}") String adminEmail,
                       @Value("${app.admin.password:}") String adminPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.info("ADMIN_EMAIL/ADMIN_PASSWORD no configurados; no se crea administrador inicial.");
            return;
        }
        if (adminPassword.length() < 8) {
            log.warn("ADMIN_PASSWORD debe tener al menos 8 caracteres; no se crea administrador inicial.");
            return;
        }
        if (usuarioRepository.findByEmail(adminEmail).isPresent()) {
            log.info("El administrador inicial ya existe; no se modifica.");
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre("Administrador");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRol("ADMIN");
        usuarioRepository.save(admin);
        log.info("Administrador inicial creado: {}", adminEmail);
    }
}
