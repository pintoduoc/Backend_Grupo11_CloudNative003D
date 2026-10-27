package com.duoc.msandesstayreservations.messaging;

import com.duoc.msandesstayreservations.dto.NotificationEvent;
import com.duoc.msandesstayreservations.model.Reserva;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchanges.direct}")
    private String directExchange;

    @Value("${app.rabbitmq.exchanges.topic}")
    private String topicExchange;

    @Value("${app.rabbitmq.routing.email-direct}")
    private String emailDirectKey;

    @Value("${app.rabbitmq.routing.email-topic-status}")
    private String emailTopicStatusKey;

    @Value("${app.rabbitmq.routing.voucher-direct}")
    private String voucherDirectKey;

    @Value("${app.rabbitmq.routing.voucher-topic-confirmed}")
    private String voucherTopicConfirmedKey;

    @Value("${app.rabbitmq.routing.housekeeping-direct}")
    private String housekeepingDirectKey;

    @Value("${app.rabbitmq.routing.housekeeping-topic-checkin}")
    private String housekeepingTopicCheckinKey;

    @Value("${app.rabbitmq.routing.housekeeping-topic-checkout}")
    private String housekeepingTopicCheckoutKey;

    public void publishReservationEvent(Reserva reserva, String actionType) {
        try {
            String traceId = UUID.randomUUID().toString();
            Map<String, Object> payload = new HashMap<>();
            payload.put("reservaId", reserva.getId());
            payload.put("estado", reserva.getEstado() != null ? reserva.getEstado().name() : "SIN_ESTADO");
            payload.put("nombreHabitacion", reserva.getNombreHabitacion());
            payload.put("nombreHuesped", reserva.getNombreHuesped());
            payload.put("cantidadHuespedes", reserva.getCantidadHuespedes());

            String eventType = resolveEventType(reserva, actionType);

            // 1. Notificación por Email: usa DirectExchange al crear y TopicExchange (email.*) al cambiar estado
            String emailExchange = "RESERVA_CREADA".equals(actionType) ? directExchange : topicExchange;
            String emailKey = "RESERVA_CREADA".equals(actionType) ? emailDirectKey : emailTopicStatusKey;
            NotificationEvent emailEvent = buildEnvelope(eventType, traceId, reserva.getId(), payload);
            rabbitTemplate.convertAndSend(emailExchange, emailKey, emailEvent);
            log.info("[PRODUCER] Evento de email enviado a exchange='{}', routingKey='{}', eventId={}",
                    emailExchange, emailKey, emailEvent.getEventId());

            // 2. Generación de Voucher: al crear (DirectExchange -> voucher.gen) o confirmar (TopicExchange -> voucher.confirmed)
            if (reserva.getEstado() == Reserva.Estado.CREADA) {
                NotificationEvent voucherEvent = buildEnvelope(eventType, traceId, reserva.getId(), payload);
                rabbitTemplate.convertAndSend(directExchange, voucherDirectKey, voucherEvent);
                log.info("[PRODUCER] Comando de voucher enviado a exchange='{}', routingKey='{}', eventId={}",
                        directExchange, voucherDirectKey, voucherEvent.getEventId());
            } else if (reserva.getEstado() == Reserva.Estado.CONFIRMADA) {
                NotificationEvent voucherEvent = buildEnvelope(eventType, traceId, reserva.getId(), payload);
                rabbitTemplate.convertAndSend(topicExchange, voucherTopicConfirmedKey, voucherEvent);
                log.info("[PRODUCER] Evento de voucher confirmado enviado a exchange='{}', routingKey='{}', eventId={}",
                        topicExchange, voucherTopicConfirmedKey, voucherEvent.getEventId());
            }

            // 3. Tickets de Housekeeping: usa DirectExchange y TopicExchange (housekeeping.#)
            if (reserva.getEstado() == Reserva.Estado.CHECKIN_PENDIENTE) {
                NotificationEvent hkDirectEvent = buildEnvelope(eventType, traceId, reserva.getId(), payload);
                rabbitTemplate.convertAndSend(directExchange, housekeepingDirectKey, hkDirectEvent);
                log.info("[PRODUCER] Ticket de preparación check-in enviado a exchange='{}', routingKey='{}', eventId={}",
                        directExchange, housekeepingDirectKey, hkDirectEvent.getEventId());
            } else if (reserva.getEstado() == Reserva.Estado.CHECKOUT) {
                NotificationEvent hkTopicEvent = buildEnvelope(eventType, traceId, reserva.getId(), payload);
                rabbitTemplate.convertAndSend(topicExchange, housekeepingTopicCheckoutKey, hkTopicEvent);
                log.info("[PRODUCER] Evento de limpieza por check-out enviado a exchange='{}', routingKey='{}', eventId={}",
                        topicExchange, housekeepingTopicCheckoutKey, hkTopicEvent.getEventId());
            }
        } catch (Exception ex) {
            log.error("[PRODUCER] Error publicando evento en RabbitMQ para reserva {}: {}",
                    reserva.getId(), ex.getMessage());
        }
    }

    private String resolveEventType(Reserva reserva, String actionType) {
        String detalles = reserva.getDetalles() != null ? reserva.getDetalles() : "";
        String huesped = reserva.getNombreHuesped() != null ? reserva.getNombreHuesped() : "";

        if ("FORCE_ERROR".equalsIgnoreCase(detalles) || "FORCE_ERROR".equalsIgnoreCase(huesped)) {
            return "FORCE_ERROR";
        }
        if ("TRANSIENT_ERROR".equalsIgnoreCase(detalles) || "TRANSIENT_ERROR".equalsIgnoreCase(huesped)) {
            return "TRANSIENT_ERROR";
        }
        return actionType + "_" + reserva.getEstado();
    }

    private NotificationEvent buildEnvelope(String type, String traceId, Long reservaId, Map<String, Object> payload) {
        return NotificationEvent.builder()
                .type(type)
                .eventId(UUID.randomUUID().toString())
                .timestamp(Instant.now().toString())
                .traceId(traceId)
                .correlationId(reservaId != null ? reservaId.toString() : "N/A")
                .payload(payload)
                .build();
    }
}
