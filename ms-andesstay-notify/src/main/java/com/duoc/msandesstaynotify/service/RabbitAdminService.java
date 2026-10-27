package com.duoc.msandesstaynotify.service;

import com.duoc.msandesstaynotify.dto.BindingRequest;
import com.duoc.msandesstaynotify.dto.CreateExchangeRequest;
import com.duoc.msandesstaynotify.dto.CreateQueueRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;

    public String createQueue(CreateQueueRequest request) {
        QueueBuilder builder = request.isDurable()
                ? QueueBuilder.durable(request.getName())
                : QueueBuilder.nonDurable(request.getName());

        if (request.getDeadLetterExchange() != null && !request.getDeadLetterExchange().isBlank()) {
            builder.withArgument("x-dead-letter-exchange", request.getDeadLetterExchange());
        }
        if (request.getDeadLetterRoutingKey() != null && !request.getDeadLetterRoutingKey().isBlank()) {
            builder.withArgument("x-dead-letter-routing-key", request.getDeadLetterRoutingKey());
        }

        Queue queue = builder.build();
        String declaredName = rabbitAdmin.declareQueue(queue);
        log.info("[RABBIT-ADMIN] Cola creada/declarada exitosamente: {}", declaredName);
        return declaredName;
    }

    public boolean deleteQueue(String queueName) {
        boolean deleted = rabbitAdmin.deleteQueue(queueName);
        log.info("[RABBIT-ADMIN] Solicitud de eliminación de cola '{}': resultado={}", queueName, deleted);
        return deleted;
    }

    public int purgeQueue(String queueName) {
        int purgedCount = rabbitAdmin.purgeQueue(queueName);
        log.info("[RABBIT-ADMIN] Cola '{}' purgada. Mensajes eliminados={}", queueName, purgedCount);
        return purgedCount;
    }

    public Properties getQueueProperties(String queueName) {
        return rabbitAdmin.getQueueProperties(queueName);
    }

    public void createExchange(CreateExchangeRequest request) {
        Exchange exchange = switch (request.getType().toLowerCase()) {
            case "direct" -> new DirectExchange(request.getName(), request.isDurable(), false);
            case "topic" -> new TopicExchange(request.getName(), request.isDurable(), false);
            case "fanout" -> new FanoutExchange(request.getName(), request.isDurable(), false);
            default -> throw new IllegalArgumentException("Tipo de exchange no soportado: " + request.getType());
        };
        rabbitAdmin.declareExchange(exchange);
        log.info("[RABBIT-ADMIN] Exchange '{}' de tipo '{}' declarado exitosamente", request.getName(), request.getType());
    }

    public boolean deleteExchange(String exchangeName) {
        boolean deleted = rabbitAdmin.deleteExchange(exchangeName);
        log.info("[RABBIT-ADMIN] Exchange '{}' eliminado: resultado={}", exchangeName, deleted);
        return deleted;
    }

    public void createBinding(BindingRequest request) {
        Binding binding = new Binding(
                request.getQueueName(),
                Binding.DestinationType.QUEUE,
                request.getExchangeName(),
                request.getRoutingKey(),
                Collections.emptyMap()
        );
        rabbitAdmin.declareBinding(binding);
        log.info("[RABBIT-ADMIN] Binding creado: Exchange '{}' -> Cola '{}' con routingKey '{}'",
                request.getExchangeName(), request.getQueueName(), request.getRoutingKey());
    }

    public void deleteBinding(BindingRequest request) {
        Binding binding = new Binding(
                request.getQueueName(),
                Binding.DestinationType.QUEUE,
                request.getExchangeName(),
                request.getRoutingKey(),
                Collections.emptyMap()
        );
        rabbitAdmin.removeBinding(binding);
        log.info("[RABBIT-ADMIN] Binding eliminado: Exchange '{}' -X-> Cola '{}' con routingKey '{}'",
                request.getExchangeName(), request.getQueueName(), request.getRoutingKey());
    }
}
