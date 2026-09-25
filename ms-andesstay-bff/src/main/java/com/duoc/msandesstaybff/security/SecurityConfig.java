package com.duoc.msandesstaybff.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableMethodSecurity 
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //CSRF(Cross-Site Request Forgery) deshabilitado evita el adjuntado de sesion via Cookie y que este pueda ser expuesto
                .csrf(csrf -> csrf.disable())
                //Configuramos el CORS con detalle específico
                .cors(cors -> cors.configurationSource(request -> CorsConfigurationSource()))
                //Evitamos la generacion de un session en el navegador , solo queremos el manejo de un token de validacion otorgado via IAM
                //Por esta razon generamos una plitica Stateless , la cual no maneja ni sesion ni estados en la sesion
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Este apartado nos permite definir quien y que es necesario para acceder a cada punto del sistema/backend
                .authorizeHttpRequests(auth -> auth
                        //Toda consulta PUT, POST y DELETE hacia /api/reservations debe tener rol Recepcionista
                        .requestMatchers(HttpMethod.PUT,"/api/reservations/**").hasRole("Recepcionista")
                        .requestMatchers(HttpMethod.POST,"/api/reservations/**").hasRole("Recepcionista")
                        .requestMatchers(HttpMethod.DELETE,"/api/reservations/**").hasRole("Recepcionista")
                        //Toda consulta PUT, POST y DELETE hacia /api/reservations debe tener rol Recepcionista
                        .requestMatchers(HttpMethod.PUT,"/api/reservations/**").hasRole("Huesped")
                        .requestMatchers(HttpMethod.POST,"/api/reservations/**").hasRole("Huesped")
                        .requestMatchers(HttpMethod.DELETE,"/api/reservations/**").hasRole("Huesped")
                        //Toda consulta PUT, HOST y DELETE hacia /api/catalog debe tener rol ADMIN:
                        .requestMatchers(HttpMethod.PUT,"/api/catalog/**").hasRole("Admin")
                        .requestMatchers(HttpMethod.POST,"/api/catalog/**").hasRole("Admin")
                        .requestMatchers(HttpMethod.DELETE,"/api/catalog/**").hasRole("Admin")
                        //Para todas las demás deben estar logueados
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                );

        return http.build();
    }

    private CorsConfiguration CorsConfigurationSource() {
        //Configuraciones base para CORS(Cross-Origin Resource Sharing)
        //Nos permite definir que configuraciones tendremos sobre el CORS de nuestras solicitudes
        //QUien la envia - Que metodos esperamos o tendremos
        //Que headers esperamos
        //Con esto estandarizamos y protegemos la información que debemos recibir y cuál responderemos
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5500"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        return config;
    }
}
