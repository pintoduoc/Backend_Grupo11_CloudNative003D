package com.duoc.msandesstaybff.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableMethodSecurity 
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF deshabilitado para arquitecturas Stateless
                .csrf(csrf -> csrf.disable())

                // Configuramos el CORS con detalle específico
                .cors(cors -> cors.configurationSource(request -> CorsConfigurationSource()))

                // Política Stateless: no maneja sesión en el servidor
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // 1. Permitir siempre las peticiones OPTIONS para evitar bloqueos de CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Rutas de Reservas: Pueden acceder Recepcionistas o Huéspedes
                        .requestMatchers(HttpMethod.PUT,"/api/reservations/**").hasAnyRole("Recepcionista", "Huesped")
                        .requestMatchers(HttpMethod.POST,"/api/reservations/**").hasAnyRole("Recepcionista", "Huesped")
                        .requestMatchers(HttpMethod.DELETE,"/api/reservations/**").hasAnyRole("Recepcionista", "Huesped")

                        // 3. Rutas de Catálogo y Administración RabbitMQ: Solo Admin
                        .requestMatchers(HttpMethod.PUT, "/api/catalog/**").hasRole("Admin")
                        .requestMatchers(HttpMethod.POST, "/api/catalog/**").hasRole("Admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/catalog/**").hasRole("Admin")
                        .requestMatchers("/api/rabbitmq/**").hasRole("Admin")

                        // 4. Para todas las demás consultas GET, solo basta con estar autenticado
                        .anyRequest().authenticated()
                )
                // Inyectamos el conversor personalizado para que Spring entienda los roles de Azure
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    // Bean para convertir el arreglo "roles" de Azure AD a los "ROLE_" de Spring Security
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    private CorsConfiguration CorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "https://frontend-cloudnative.vercel.app",
                "http://localhost:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "origin", "accept"));
        return config;
    }
}
