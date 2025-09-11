package com.noctua.commons.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.UUID;
import java.util.Optional;

/**
 * Resuelve el tenantId basado en diferentes estrategias.
 * Componente genérico reutilizable en todos los microservicios.
 */
@Component
public class TenantResolver {
    
    private final RestTemplate restTemplate;
    private final String tenancyServiceUrl;
    
    @Autowired
    public TenantResolver(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // URL del servicio de tenancy (puede venir de configuración)
        this.tenancyServiceUrl = "http://tenancy-service/api/tenants";
    }
    
    /**
     * Resuelve el tenantId desde el host/subdomain.
     * Ejemplo: empresa-abc.noctua.com → busca tenant con subdomain "empresa-abc"
     */
    public Optional<UUID> resolveFromHost(String host) {
        try {
            String subdomain = extractSubdomain(host);
            if (subdomain == null || subdomain.isEmpty()) {
                return Optional.empty();
            }
            
            String url = tenancyServiceUrl + "/by-subdomain/" + subdomain;
            TenantDto tenant = restTemplate.getForObject(url, TenantDto.class);
            
            return tenant != null ? Optional.of(tenant.getId()) : Optional.empty();
            
        } catch (RestClientException e) {
            // Log error pero no fallar
            System.err.println("Error resolving tenant from host: " + host + " - " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Resuelve el tenantId desde el header HTTP.
     * Útil para APIs internas entre microservicios.
     */
    public Optional<UUID> resolveFromHeader(String tenantHeader) {
        try {
            if (tenantHeader == null || tenantHeader.trim().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(UUID.fromString(tenantHeader.trim()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Extrae el subdomain del host.
     * empresa-abc.noctua.com → "empresa-abc"
     * localhost → null (para desarrollo)
     */
    private String extractSubdomain(String host) {
        if (host == null || host.equals("localhost") || host.startsWith("127.0.0.1")) {
            return null; // Desarrollo local
        }
        
        String[] parts = host.split("\\.");
        if (parts.length >= 3) {
            return parts[0]; // Primer parte es el subdomain
        }
        
        return null;
    }
    
    /**
     * DTO simple para recibir datos del tenant service
     */
    public static class TenantDto {
        private UUID id;
        private String name;
        private String subdomain;
        
        // Constructors
        public TenantDto() {}
        
        public TenantDto(UUID id, String name, String subdomain) {
            this.id = id;
            this.name = name;
            this.subdomain = subdomain;
        }
        
        // Getters y Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getSubdomain() { return subdomain; }
        public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
    }
}
