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
public class VoucherConsumer {

    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = "${app.rabbitmq.queues.voucher}")
    public void procesarVoucher(
            NotificationEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(AmqpHeaders.REDELIVERED) boolean redelivered) throws IOException {

        try {
            log.info("[VOUCHER-CONSUMER] Solicitud de voucher recibida: eventId={}, correlationId={}, redelivered={}",
                    event.getEventId(), event.getCorrelationId(), redelivered);

            if (event.getEventId() == null || event.getPayload() == null || "FORCE_ERROR".equalsIgnoreCase(event.getType())) {
                throw new IllegalArgumentException("Datos de voucher incompletos o fallo no recuperable (FORCE_ERROR)");
            }

            if ("TRANSIENT_ERROR".equalsIgnoreCase(event.getType())) {
                throw new IllegalStateException("Motor de generación PDF temporalmente ocupado (TRANSIENT_ERROR)");
            }

            if (!processedEvents.add(event.getEventId())) {
                log.warn("[VOUCHER-CONSUMER] Voucher ya generado previamente (eventId={}). ACK idempotente.", event.getEventId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            log.info("[VOUCHER-CONSUMER] Comprobante PDF/Voucher emitido para reserva #{} - Huésped: {}",
                    event.getCorrelationId(), event.getPayload().get("nombreHuesped"));

            channel.basicAck(deliveryTag, false);
            log.info("[VOUCHER-CONSUMER] ACK confirmado (deliveryTag={})", deliveryTag);

        } catch (IllegalStateException transientEx) {
            if (!redelivered) {
                log.warn("[VOUCHER-CONSUMER] Error recuperable (deliveryTag={}): {}. Reintentando (requeue=true).",
                        deliveryTag, transientEx.getMessage());
                channel.basicNack(deliveryTag, false, true);
            } else {
                log.error("[VOUCHER-CONSUMER] Reintento agotado al generar voucher (deliveryTag={}): {}. Enviando a DLQ (requeue=false).",
                        deliveryTag, transientEx.getMessage());
                channel.basicNack(deliveryTag, false, false);
            }
        } catch (Exception fatalEx) {
            log.error("[VOUCHER-CONSUMER] Error no recuperable al generar voucher (deliveryTag={}): {}. Enviando a DLQ (requeue=false).",
                    deliveryTag, fatalEx.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
