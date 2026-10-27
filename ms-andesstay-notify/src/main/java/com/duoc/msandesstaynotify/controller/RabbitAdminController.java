package com.duoc.msandesstaynotify.controller;

import com.duoc.msandesstaynotify.dto.BindingRequest;
import com.duoc.msandesstaynotify.dto.CreateExchangeRequest;
import com.duoc.msandesstaynotify.dto.CreateQueueRequest;
import com.duoc.msandesstaynotify.service.RabbitAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api/rabbitmq")
@RequiredArgsConstructor
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    @PostMapping("/queues")
    public ResponseEntity<Map<String, Object>> createQueue(@Valid @RequestBody CreateQueueRequest request) {
        String queueName = rabbitAdminService.createQueue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "CREATED",
                "queue", queueName,
                "durable", request.isDurable()
        ));
    }

    @GetMapping("/queues/{name}")
    public ResponseEntity<Map<String, Object>> getQueueInfo(@PathVariable("name") String name) {
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre de la cola es obligatorio"));
        }
        Properties props = rabbitAdminService.getQueueProperties(name);
        if (props == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "La cola '" + name + "' no existe en el clúster"));
        }
        return ResponseEntity.ok(Map.of(
                "queue", name,
                "messageCount", props.get("QUEUE_MESSAGE_COUNT"),
                "consumerCount", props.get("QUEUE_CONSUMER_COUNT")
        ));
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Map<String, Object>> deleteQueue(@PathVariable("name") String name) {
        boolean deleted = rabbitAdminService.deleteQueue(name);
        return ResponseEntity.ok(Map.of(
                "queue", name,
                "deleted", deleted
        ));
    }

    @DeleteMapping("/queues/{name}/purge")
    public ResponseEntity<Map<String, Object>> purgeQueue(@PathVariable("name") String name) {
        int purged = rabbitAdminService.purgeQueue(name);
        return ResponseEntity.ok(Map.of(
                "queue", name,
                "purgedMessages", purged
        ));
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, Object>> createExchange(@Valid @RequestBody CreateExchangeRequest request) {
        rabbitAdminService.createExchange(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "CREATED",
                "exchange", request.getName(),
                "type", request.getType()
        ));
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Map<String, Object>> deleteExchange(@PathVariable("name") String name) {
        boolean deleted = rabbitAdminService.deleteExchange(name);
        return ResponseEntity.ok(Map.of(
                "exchange", name,
                "deleted", deleted
        ));
    }

    @PostMapping("/bindings")
    public ResponseEntity<Map<String, Object>> createBinding(@Valid @RequestBody BindingRequest request) {
        rabbitAdminService.createBinding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "BOUND",
                "exchange", request.getExchangeName(),
                "queue", request.getQueueName(),
                "routingKey", request.getRoutingKey()
        ));
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Map<String, Object>> deleteBinding(@Valid @RequestBody BindingRequest request) {
        rabbitAdminService.deleteBinding(request);
        return ResponseEntity.ok(Map.of(
                "status", "UNBOUND",
                "exchange", request.getExchangeName(),
                "queue", request.getQueueName(),
                "routingKey", request.getRoutingKey()
        ));
    }

    // Manejador de errores de validación (@Valid) para responder HTTP 400 claro
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Configuración de RabbitMQ inválida",
                "details", errors
        ));
    }
}
