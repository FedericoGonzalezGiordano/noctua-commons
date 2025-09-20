package com.noctua.commons.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Record para datos de plan - inmutable y conciso.
 * Usado para comunicación entre microservicios.
 */
public record PlanDto(
    UUID id,
    String name,
    String planKey,
    String description,
    BigDecimal priceMonthly,
    BigDecimal priceAnnually,
    Boolean isActive,
    Integer displayOrder,
    Map<String, Object> features,
    Instant createdAt,
    Instant updatedAt,
    
    // Campos calculados
    BigDecimal annualSavings,
    String priceDisplayMonthly,
    String priceDisplayAnnually,
    Boolean isFree,
    Boolean isPopular
) {
    
    /**
     * Constructor simplificado para casos básicos.
     */
    public PlanDto(UUID id, String name, String planKey) {
        this(id, name, planKey, null, null, null, true, null, null, null, null, null, null, null, false, false);
    }
    
    /**
     * Verifica si el plan está activo.
     */
    public boolean isActivePlan() {
        return Boolean.TRUE.equals(isActive);
    }
    
    /**
     * Verifica si es un plan gratuito.
     */
    public boolean isFreePlan() {
        return Boolean.TRUE.equals(isFree) || 
               (priceMonthly != null && priceMonthly.compareTo(BigDecimal.ZERO) == 0);
    }
}
