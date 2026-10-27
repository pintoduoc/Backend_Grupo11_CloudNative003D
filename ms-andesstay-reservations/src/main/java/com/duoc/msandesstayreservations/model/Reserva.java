package com.duoc.msandesstayreservations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(name = "reserva")
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    public enum Estado {
        CREADA,
        CONFIRMADA,
        CHECKIN_PENDIENTE,
        EN_ESTADIA,
        CHECKOUT,
        CANCELADA
    }
    @Column(nullable = false)
    private String nombreHabitacion;

    @Column(nullable = false)
    private String nombreHuesped;

    @Column(nullable = true)
    private String detalles;

    @Column(nullable = false)
    private Long cantidadHuespedes;

    @Column(nullable = false)
    private Date fechaInicio;

    @Column(nullable = false)
    private Date fechaTermino;
}
