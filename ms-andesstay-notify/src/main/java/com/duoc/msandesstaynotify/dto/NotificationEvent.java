package com.duoc.msandesstaynotify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String type;          // Ejmeplo: "RESERVA_CREADA_CREADA", "FORCE_ERROR", "TRANSIENT_ERROR"
    private String eventId;       // UUID único del evento (usado para idempotencia)
    private String timestamp;     // ISO-8601
    private String traceId;       // ID de trazabilidad distribuida
    private String correlationId; // ID de la reserva asociada
    private Map<String, Object> payload; // Datos del huésped, habitación, estado, etc.
}