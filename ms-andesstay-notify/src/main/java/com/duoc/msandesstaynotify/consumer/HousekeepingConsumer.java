package com.duoc.msandesstaynotify.consumer;

import com.duoc.msandesstaynotify.dto.NotificationEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HousekeepingConsumer {

    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = "${app.rabbitmq.queues.housekeeping}")
    public void procesarHousekeeping(
            NotificationEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(AmqpHeaders.REDELIVERED) boolean redelivered) throws IOException {

        try {
            log.info("[HOUSEKEEPING-CONSUMER] Ticket recibido: eventId={}, type={}, redelivered={}",
                    event.getEventId(), event.getType(), redelivered);

            if (event.getEventId() == null || event.getPayload() == null || "FORCE_ERROR".equalsIgnoreCase(event.getType())) {
                throw new IllegalArgumentException("Ticket de housekeeping inválido o fallo no recuperable (FORCE_ERROR)");
            }

            if ("TRANSIENT_ERROR".equalsIgnoreCase(event.getType())) {
                throw new IllegalStateException("Error temporal al asignar personal de limpieza (TRANSIENT_ERROR)");
            }

            if (!processedEvents.add(event.getEventId())) {
                log.warn("[HOUSEKEEPING-CONSUMER] Ticket duplicado (eventId={}). Ignorando con ACK idempotente.", event.getEventId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            log.info("[HOUSEKEEPING-CONSUMER] Orden de preparación/limpieza generada para habitación: {}",
                    event.getPayload().get("nombreHabitacion"));

            channel.basicAck(deliveryTag, false);
            log.info("[HOUSEKEEPING-CONSUMER] ACK confirmado (deliveryTag={})", deliveryTag);

        } catch (IllegalStateException transientEx) {
            if (!redelivered) {
                log.warn("[HOUSEKEEPING-CONSUMER] Error recuperable (deliveryTag={}): {}. Reintentando (requeue=true).",
                        deliveryTag, transientEx.getMessage());
                channel.basicNack(deliveryTag, false, true);
            } else {
                log.error("[HOUSEKEEPING-CONSUMER] Reintento agotado en ticket (deliveryTag={}): {}. Enviando a DLQ (requeue=false).",
                        deliveryTag, transientEx.getMessage());
                channel.basicNack(deliveryTag, false, false);
            }
        } catch (Exception fatalEx) {
            log.error("[HOUSEKEEPING-CONSUMER] Error no recuperable en ticket (deliveryTag={}): {}. Rechazando hacia DLQ (requeue=false).",
                    deliveryTag, fatalEx.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
