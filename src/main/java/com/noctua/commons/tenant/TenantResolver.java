package com.noctua.commons.tenant;

import com.noctua.commons.services.TenantCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.Optional;

/**
 * Resuelve el tenantId basado en diferentes estrategias usando cache local.
 * Componente genérico reutilizable en todos los microservicios.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantResolver {
    
    private final TenantCacheService tenantCacheService;
    
    /**
     * Resuelve el tenantId desde el host/subdomain usando cache local.
     * Ejemplo: empresa-abc.noctua.com → busca tenant con subdomain "empresa-abc"
     */
    public Optional<UUID> resolveFromHost(String host) {
        try {
            String subdomain = extractSubdomain(host);
            if (subdomain == null || subdomain.isEmpty()) {
                return Optional.empty();
            }
            
            // Usar cache local - NO bloquea el hilo
            return tenantCacheService.getTenantIdBySubdomain(subdomain);
            
        } catch (Exception e) {
            log.warn("Error resolving tenant from host: {} - {}", host, e.getMessage());
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
}
