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
    private String direccion; //Calle y numero


    @NotNull
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    public enum Tipo {
        HABITACION,
        CABANA
    }

    @Nullable
    private Long numeroHabitacion;

    @NotNull
    private Integer unidadesRestantes;

    @NotNull
    private Integer unidadesTotales;

    @NotNull
    private boolean disponibilidad;

    @NotNull
    private Double precio;
}
