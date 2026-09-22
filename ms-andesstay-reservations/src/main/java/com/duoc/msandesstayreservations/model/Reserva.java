package com.duoc.msandesstayreservations.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

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

    @NotNull
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
    @NotNull
    private String nombreHabitacion;

    @NotNull
    private String nombreHuesped;

    @Nullable
    private String detalles;

    @NotNull
    private Long cantidadHuespedes;

    @NotNull
    private Date fechaInicio;

    @NotNull
    private Date fechaTermino;
}
