package com.cantero.sistema_inventario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties({"facturas"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"facturas"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("factura-items")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<FacturaItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("factura-movimientos")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Movimiento> movimientos = new ArrayList<>();
}