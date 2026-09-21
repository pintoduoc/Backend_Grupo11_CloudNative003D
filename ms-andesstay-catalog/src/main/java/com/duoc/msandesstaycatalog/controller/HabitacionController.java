package com.duoc.msandesstaycatalog.controller;

import com.duoc.msandesstaycatalog.model.Habitacion;
import com.duoc.msandesstaycatalog.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/catalog")
public class HabitacionController {
    @Autowired
    private HabitacionService habitacionService;

    //Obtener todas las habitaciones. No se requiere ningún parámetro ni cuerpo para esta consulta.
    @GetMapping
    public List<Habitacion> findAllHabitaciones() {
        return habitacionService.findAll();
    }


    //Obtener habitacion por ID (Ejemplo: /api/catalog/id/1)
    @GetMapping("/id/{id}")
    public Habitacion findHabitacionById(@PathVariable("id") Long id) {
        return habitacionService.findById(id);
    }
    //Obtener todas las habitaciones con la misma direccion (Ejemplo: /api/catalog/direccion/"Antonio Varas 666")
    @GetMapping("/direccion/{direccion}")
    public List<Habitacion> findHabitacionByDireccion(@PathVariable("direccion") String direccion) {
        return habitacionService.findByDireccion(direccion);
    }
    //Obtener todas las habitaciones del mismo tipo (Ejemplo: /api/catalog/tipo/CABANA)
    @GetMapping("/tipo/{tipo}")
    public List<Habitacion> findHabitacionByTipo(@PathVariable("tipo") Habitacion.Tipo tipo) {
        return habitacionService.findByTipo(tipo);
    }

    //Obtener todas las habitaciones con el mismo numero de habitacion (Ejemplo: /api/catalog/numeroHabitacion/512)
    @GetMapping("/numeroHabitacion/{numeroHabitacion}")
    public List<Habitacion> findHabitacionByNumeroHabitacion(@PathVariable("numeroHabitacion") Long numeroHabitacion) {
        return habitacionService.findByNumeroHabitacion(numeroHabitacion);
    }

    //Obtener todas las habitaciones con la misma disponibilidad (Ejemplo para obtener todas las habitaciones con disponibilidad: /api/catalog/disponibilidad/true)
    @GetMapping("/disponibilidad/{disponibilidad}")
    public List<Habitacion> findHabitacionByDisponibilidad(@PathVariable("disponibilidad") Boolean disponibilidad) {
        return habitacionService.findByDisponibilidad(disponibilidad);
    }
/*
    Crear Habitacion. Se debe enviar la habitacion en el cuerpo de la solicitud.

    El cuerpo debe contener todos los campos de la habitacion, excepto el id y numeroHabitacion (opcional).
    Los campos a ingresar son: direccion, tipo (HABITACION / CABANA), numeroHabitacion (opcional), unidadesRestantes, unidadesTotales, disponibilidad.
    Ejemplo de cuerpo de solicitud:
    {
        "direccion": "Calle Principal",
        "tipo": "HABITACION",
        "numeroHabitacion": 512,
        "unidadesRestantes": 10,
        "unidadesTotales": 20,
        "disponibilidad": true
    }
*/
    @PostMapping
    public Habitacion createHabitacion(@RequestBody Habitacion habitacion) {
        return habitacionService.save(habitacion);
    }

    //Actualizar habitacion por id. Se debe enviar el id de la habitacion a actualizar en la URL y la habitacion actualizadoa en el cuerpo de la solicitud (sin id en el cuerpo)
    @PutMapping("/{id}")
    public Habitacion updateHabitacion(@PathVariable("id") Long id, @RequestBody Habitacion habitacion) {
        habitacion.setId(id);
        return habitacionService.save(habitacion);
    }

    //Actualizar precio de habitacion por id. Se debe enviar el id de la habitacion a actualizar en la URL y el precio actualizado en el cuerpo de la solicitud
    @PutMapping("/precio/{id}")
    public Habitacion updatePrecioByID(@PathVariable("id") Long id, @RequestBody Double precio) {
        return habitacionService.updatePrecioByID(id, precio);
    }

    //Actualizar disponibilidad de habitacion por id. Se debe enviar el id de la habitacion a actualizar en la URL y la disponibilidad actualizada en el cuerpo de la solicitud
    @PutMapping("/disponibilidad/{id}")
    public Habitacion updateDisponibilidadByID(@PathVariable("id") Long id, @RequestBody boolean disponibilidad) {
        return habitacionService.updateDisponibilidadByID(id, disponibilidad);
    }

    //Eliminar habitacion por ID. (ejemplo /api/catalog/id/1)
    @DeleteMapping("/id/{id}")
    public void deleteHabitacion(@PathVariable("id") Long id) {
        habitacionService.deleteById(id);
    }
}
