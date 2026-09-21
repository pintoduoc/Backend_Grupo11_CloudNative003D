package com.duoc.msandesstaycatalog.service;

import com.duoc.msandesstaycatalog.model.Habitacion;
import com.duoc.msandesstaycatalog.repositories.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitacionService {
    @Autowired
    private HabitacionRepository habitacionRepository;

    public List<Habitacion> findAll(){
        return habitacionRepository.findAll();
    }

    public Habitacion findById(Long id){
        return habitacionRepository.findById(id).orElse(null);
    }

    public List<Habitacion> findByDireccion (String direccion){
        return habitacionRepository.findByDireccion(direccion);
    }

    public List<Habitacion> findByTipo(Habitacion.Tipo tipo){
        return habitacionRepository.findByTipo(tipo);
    }

    public List<Habitacion> findByNumeroHabitacion(Long numeroHabitacion){
        return habitacionRepository.findByNumeroHabitacion(numeroHabitacion);
    }

    public List<Habitacion> findByDisponibilidad(Boolean disponibilidad) {
        return habitacionRepository.findByDisponibilidad(disponibilidad);
    }

    public Habitacion save(Habitacion habitacion){
        return habitacionRepository.save(habitacion);
    }

    public void deleteById(Long id){
        habitacionRepository.deleteById(id);
    }

    public Habitacion updatePrecioByID(Long id, Double precio){
        Habitacion habitacion = habitacionRepository.findById(id).orElse(null);
        if (habitacion != null){
            habitacion.setPrecio(precio);
            return habitacionRepository.save(habitacion);
        }
        return null;
    }

    public Habitacion updateDisponibilidadByID(Long id, boolean disponibilidad){
        Habitacion habitacion = habitacionRepository.findById(id).orElse(null);
        if (habitacion != null){
            habitacion.setDisponibilidad(disponibilidad);
            return habitacionRepository.save(habitacion);
        }
        return null;
    }
}
