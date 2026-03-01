package com.cantero.sistema_inventario.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    // ENTRADA o SALIDA
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    private Double precioUnitario;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id")
    private Producto producto;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = true)
    @JsonBackReference("factura-movimientos")
    private Factura factura;
}