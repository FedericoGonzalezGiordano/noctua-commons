package com.noctua.commons.dto;

import java.util.UUID;

/**
 * Record para datos de tenant - inmutable y conciso.
 * Usado para comunicación entre microservicios.
 */
public record TenantDto(
    UUID id,
    String name,
    String subdomain,
    String status,
    String planType
) {
    
    /**
     * Constructor para casos donde solo se necesita el ID.
     */
    public TenantDto(UUID id) {
        this(id, null, null, null, null);
    }
    
    /**
     * Verifica si el tenant está activo.
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    /**
     * Verifica si el record tiene datos válidos.
     */
    public boolean isValid() {
        return id != null;
    }
}
