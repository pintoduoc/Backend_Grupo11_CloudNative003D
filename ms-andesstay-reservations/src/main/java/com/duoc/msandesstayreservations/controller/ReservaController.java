package com.duoc.msandesstayreservations.controller;

import com.duoc.msandesstayreservations.model.Reserva;
import com.duoc.msandesstayreservations.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservaController {
    @Autowired
    private ReservaService reservaService;

    //Obtener todas las reservas, no se requiere ningun parametro ni cuerpo para esta consulta.
    @GetMapping
    public List<Reserva> findAll() {
        return reservaService.findAll();
    }

    //Obtener Reserva por ID (Ejemplo: /api/reservations/id/1).
    @GetMapping("/id/{id}")
    public Reserva findById(@PathVariable("id") Long id) {
        return reservaService.findById(id);
    }

    //Obtener Reserva por Estado (Ejemplo: /api/reservations/estado/EN_ESTADIA).
    @GetMapping("/estado/{estado}")
    public List<Reserva> findByEstado(@PathVariable("estado") Reserva.Estado estado) {
        return reservaService.findByEstado(estado);
    }

    /*
    Crear Reserva. Se debe enviar la reserva en el cuerpo de la solicitud.

    El cuerpo debe contener todos los campos de la reserva, excepto el id y detalles (opcional).
    Los campos a ingresar son: estado, nombreHabitacion, nombreHuesped, detalles (opcional), cantidadHuespedes, fechaInicio y fechaTermino.
    Ejemplo de cuerpo de solicitud:
    {
        "estado": "EN_ESTADIA",
        "nombreHabitacion": "Cabaña Los Andes",
        "nombreHuesped": "Juan Pérez",
        "detalles": "Detalles de la reserva",
        "cantidadHuespedes": 4,
        "fechaInicio": "2023-09-01",
        "fechaTermino": "2023-09-05"
    }
*/

    @PostMapping
    public Reserva createReserva(@RequestBody Reserva reserva) {
        return reservaService.save(reserva);
    }

    //Actualizar Estado de la reserva por ID. Se debe enviar el id de la reserva a actualizar en la URL y el estado actualizado en el cuerpo de la solicitud
    @PutMapping("/estado/{id}")
    public Reserva updateEstadoById(@PathVariable("id") Long id, @RequestBody Reserva.Estado estado) {
        return reservaService.updateEstadoById(id, estado);
    }

    //Eliminar reserva por ID (Ejemplo: /api/reservations/id/1).
    @DeleteMapping("/id/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        reservaService.deleteById(id);
    }
}
