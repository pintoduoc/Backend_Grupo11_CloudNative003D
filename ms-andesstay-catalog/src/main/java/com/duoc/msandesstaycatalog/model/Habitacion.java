package com.duoc.msandesstaycatalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "habitacion")
@AllArgsConstructor
@NoArgsConstructor
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // Nombre de la habitación

    @Column(nullable = false)
    private String direccion; //Calle numero, comuna.

    @Column(nullable = true)
    private String numeroHabitacion; //Número de habitación, opcional.


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    public enum Tipo {
        HABITACION,
        CABANA
    }

    @Column(nullable = false)
    private Long capacidad;

    @Column(nullable = false)
    private boolean disponibilidad;

    @Column(nullable = false)
    private Double precio;
}
