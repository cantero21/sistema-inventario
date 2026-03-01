package com.cantero.sistema_inventario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String nombre;

    private String direccion;
    private String telefono;

    @Email(message = "El email debe tener un formato válido")
    private String email;


    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Producto> productos = new ArrayList<>();
}