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
    private Long idHabitacion;

    @Nullable
    private String detalles;

    @NotNull
    private Date fechaInicio;

    @NotNull
    private Date fechaTermino;

    //private Long idCliente; //Configurar para azure (no tengo idea como funciona)
}
