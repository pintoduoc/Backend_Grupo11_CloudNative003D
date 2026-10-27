package com.duoc.msandesstaynotify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BindingRequest {

    @NotBlank(message = "El nombre de la cola de destino es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre de la cola contiene caracteres inválidos")
    private String queueName;

    @NotBlank(message = "El nombre del exchange de origen es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre del exchange contiene caracteres inválidos")
    private String exchangeName;

    @NotBlank(message = "La routing key es obligatoria")
    private String routingKey;
}