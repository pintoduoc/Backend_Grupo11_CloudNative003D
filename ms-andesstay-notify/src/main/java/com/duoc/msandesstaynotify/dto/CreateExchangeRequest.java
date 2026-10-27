package com.duoc.msandesstaynotify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateExchangeRequest {

    @NotBlank(message = "El nombre del exchange no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre del exchange contiene caracteres inválidos")
    private String name;

    @NotBlank(message = "El tipo de exchange es obligatorio (direct, topic, fanout)")
    @Pattern(regexp = "^(direct|topic|fanout)$", message = "El tipo de exchange debe ser 'direct', 'topic' o 'fanout'")
    private String type;

    private boolean durable = true;
}
