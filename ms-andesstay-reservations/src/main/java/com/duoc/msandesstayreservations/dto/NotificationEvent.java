package com.duoc.msandesstayreservations.dto;

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
    private String type;
    private String eventId;
    private String timestamp;
    private String traceId;
    private String correlationId;
    private Map<String, Object> payload;
}
