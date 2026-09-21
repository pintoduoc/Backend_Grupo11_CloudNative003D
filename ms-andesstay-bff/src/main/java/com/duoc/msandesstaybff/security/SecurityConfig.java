package com.duoc.msandesstaybff.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Deshabilita CSRF ya que usaremos JWT (Stateless)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Reglas de autorización
                .authorizeExchange(exchanges -> exchanges
                        // Permite acceso público solo al endpoint de salud/actuator si existiera
                        .pathMatchers("/actuator/health").permitAll()

                        // Exige autenticación para cualquier otra petición
                        .anyExchange().authenticated()
                )

                // Configura este servicio como un Resource Server que valida JWTs
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }
}