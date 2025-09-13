package com.noctua.commons.services;

import com.noctua.commons.dto.TenantDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

/**
 * Servicio HTTP para comunicación con tenancy-service usando WebClient reactivo.
 * Centraliza toda la lógica de comunicación HTTP relacionada con tenants.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantHttpService {
    
    private final WebClient webClient;

    @Value("${noctua.tenancy.service.url:http://localhost:8080}")
    private String tenancyServiceUrl;
    
    /**
     * Obtiene información del tenant por subdomain usando programación reactiva.
     */
    public Mono<TenantDto> getTenantBySubdomain(String subdomain) {
        return webClient.get()
                .uri(tenancyServiceUrl + "/api/tenants/by-subdomain/{subdomain}", subdomain)
                .retrieve()
                .bodyToMono(TenantDto.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> log.warn("Error fetching tenant by subdomain '{}': {}", 
                    subdomain, error.getMessage()))
                .onErrorReturn(new TenantDto(null, null, null, null, null)); // Return empty record on error
    }
    
    /**
     * Obtiene información del tenant por ID.
     */
    public Mono<TenantDto> getTenantById(UUID tenantId) {
        return webClient.get()
                .uri(tenancyServiceUrl + "/api/tenants/{id}", tenantId)
                .retrieve()
                .bodyToMono(TenantDto.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> log.warn("Error fetching tenant by ID '{}': {}", 
                    tenantId, error.getMessage()))
                .onErrorReturn(new TenantDto(null, null, null, null, null));
    }
    
    /**
     * Verifica si un tenant existe y está activo.
     */
    public Mono<Boolean> isTenantActive(UUID tenantId) {
        return getTenantById(tenantId)
                .map(TenantDto::isActive)
                .defaultIfEmpty(false);
    }
}
