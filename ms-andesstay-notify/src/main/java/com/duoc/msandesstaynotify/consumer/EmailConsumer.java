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
public class EmailConsumer {

    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = "${app.rabbitmq.queues.email}")
    public void procesarEmail(
            NotificationEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(AmqpHeaders.REDELIVERED) boolean redelivered) throws IOException {

        try {
            log.info("[EMAIL-CONSUMER] Mensaje recibido: eventId={}, type={}, correlationId={}, redelivered={}",
                    event.getEventId(), event.getType(), event.getCorrelationId(), redelivered);

            // 1. Validación de error NO recuperable (datos inválidos o fallo definitivo simulado)
            if (event.getEventId() == null || event.getPayload() == null || "FORCE_ERROR".equalsIgnoreCase(event.getType())) {
                throw new IllegalArgumentException("Mensaje de email inválido o fallo no recuperable (FORCE_ERROR)");
            }

            // 2. Simulación de error RECUPERABLE / transitorio (ej. timeout temporal de servidor SMTP)
            if ("TRANSIENT_ERROR".equalsIgnoreCase(event.getType())) {
                throw new IllegalStateException("Fallo temporal de conexión SMTP (TRANSIENT_ERROR)");
            }

            // 3. Control de idempotencia
            if (!processedEvents.add(event.getEventId())) {
                log.warn("[EMAIL-CONSUMER] Evento duplicado detectado (eventId={}). Aplicando ACK idempotente.", event.getEventId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 4. Procesamiento de negocio y ACK explícito
            log.info("[EMAIL-CONSUMER] Enviando correo a huésped '{}' para habitación '{}' (estado={})...",
                    event.getPayload().get("nombreHuesped"),
                    event.getPayload().get("nombreHabitacion"),
                    event.getPayload().get("estado"));

            channel.basicAck(deliveryTag, false);
            log.info("[EMAIL-CONSUMER] ACK enviado exitosamente (deliveryTag={})", deliveryTag);

        } catch (IllegalStateException transientEx) {
            // Ruta A de error: Error recuperable -> Reintentar 1 vez (requeue=true); si ya fue reintentado, enviar a DLQ (requeue=false)
            if (!redelivered) {
                log.warn("[EMAIL-CONSUMER] Error recuperable (deliveryTag={}): {}. Reencolando para reintento (requeue=true).",
                        deliveryTag, transientEx.getMessage());
                channel.basicNack(deliveryTag, false, true);
            } else {
                log.error("[EMAIL-CONSUMER] Reintento agotado para mensaje (deliveryTag={}): {}. Enviando a DLQ (requeue=false).",
                        deliveryTag, transientEx.getMessage());
                channel.basicNack(deliveryTag, false, false);
            }
        } catch (Exception fatalEx) {
            // Ruta B de error: Error no recuperable -> NACK directo hacia DLX/DLQ sin reencolar (requeue=false)
            log.error("[EMAIL-CONSUMER] Error no recuperable procesando mensaje (deliveryTag={}): {}. Enviando directo a DLQ (requeue=false).",
                    deliveryTag, fatalEx.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
