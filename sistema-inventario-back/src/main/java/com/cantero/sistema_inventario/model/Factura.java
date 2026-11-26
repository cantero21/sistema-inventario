package com.cantero.sistema_inventario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private double total;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EstadoFactura estado = EstadoFactura.ACTIVA;

    private LocalDateTime fechaAnulacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_anulacion_id")
    @JsonIgnoreProperties({"facturas", "hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuarioAnulacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    @NotNull(message = "El cliente es obligatorio")
    @JsonIgnoreProperties({"facturas", "hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"facturas", "hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("factura-items")
    @NotEmpty(message = "La factura debe tener al menos un ítem")
    @Valid
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<FacturaItem> items = new ArrayList<>();

    // CAMBIO: Ahora se ignora en el JSON
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Movimiento> movimientos = new ArrayList<>();

    @PrePersist
    private void asegurarEstadoActivo() {
        if (estado == null) {
            estado = EstadoFactura.ACTIVA;
        }
    }
}