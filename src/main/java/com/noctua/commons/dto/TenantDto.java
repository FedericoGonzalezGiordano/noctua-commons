package com.noctua.commons.dto;

import com.noctua.commons.enums.TenantStatus;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Record para datos de tenant - inmutable y conciso.
 * Usado para comunicación entre microservicios.
 */
public record TenantDto(
    UUID id,
    String name,
    String subdomain,
    PlanDto plan,
    TenantStatus status,
    Map<String, Object> settings,
    Instant createdAt,
    Instant updatedAt
) {
    
    /**
     * Constructor para casos donde solo se necesita el ID.
     */
    public TenantDto(UUID id) {
        this(id, null, null, null, null, null, null, null);
    }
    
    /**
     * Constructor simplificado con datos básicos.
     */
    public TenantDto(UUID id, String name, String subdomain, TenantStatus status) {
        this(id, name, subdomain, null, status, null, null, null);
    }
    
    /**
     * Verifica si el tenant está activo.
     */
    public boolean isActive() {
        return status != null && status.isOperational();
    }
    
    /**
     * Verifica si el tenant está inactivo.
     */
    public boolean isInactive() {
        return status != null && status.isInactive();
    }
    
    /**
     * Verifica si el record tiene datos válidos.
     */
    public boolean isValid() {
        return id != null && status != null;
    }
    
    /**
     * Obtiene el nombre del plan si está disponible.
     */
    public String getPlanName() {
        return plan != null ? plan.name() : null;
    }
    
    /**
     * Obtiene la clave del plan si está disponible.
     */
    public String getPlanKey() {
        return plan != null ? plan.planKey() : null;
    }
}
