package com.duoc.msandesstaycatalog.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Data
@Table(name = "habitacion")
@AllArgsConstructor
@NoArgsConstructor
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre; // Nombre de la habitación

    @NotNull
    private String direccion; //Calle numero, comuna.

    @Nullable
    private String numeroHabitacion; //Número de habitación, opcional.


    @NotNull
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    public enum Tipo {
        HABITACION,
        CABANA
    }

    @NotNull
    private Long capacidad;

    @NotNull
    private boolean disponibilidad;

    @NotNull
    private Double precio;
}
