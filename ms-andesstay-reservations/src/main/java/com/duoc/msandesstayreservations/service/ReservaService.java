package com.duoc.msandesstayreservations.service;

import com.duoc.msandesstayreservations.messaging.ReservationEventPublisher;
import com.duoc.msandesstayreservations.model.Reserva;
import com.duoc.msandesstayreservations.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservationEventPublisher eventPublisher;

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Reserva findById(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }

    public List<Reserva> findByEstado(Reserva.Estado estado) {
        return reservaRepository.findByEstado(estado);
    }

    public Reserva save(Reserva reserva) {
        Reserva saved = reservaRepository.save(reserva);
        eventPublisher.publishReservationEvent(saved, "RESERVA_CREADA");
        return saved;
    }

    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

    public Reserva updateEstadoById(Long id, Reserva.Estado estado) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva != null) {
            reserva.setEstado(estado);
            Reserva updated = reservaRepository.save(reserva);
            eventPublisher.publishReservationEvent(updated, "ESTADO_ACTUALIZADO");
            return updated;
        }
        return null;
    }
}
