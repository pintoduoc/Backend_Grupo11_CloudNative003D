package com.duoc.msandesstaycatalog.repositories;

import com.duoc.msandesstaycatalog.model.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    List<Habitacion> findByTipo(Habitacion.Tipo tipo);

    List<Habitacion> findByDireccion(String direccion);

    List<Habitacion> findByNumeroHabitacion(Long numeroHabitacion);

    List<Habitacion> findByDisponibilidad(Boolean disponibilidad);
}
