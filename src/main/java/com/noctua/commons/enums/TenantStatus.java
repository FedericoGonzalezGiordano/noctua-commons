package com.noctua.commons.enums;

/**
 * Estados posibles de un tenant en el sistema.
 */
public enum TenantStatus {
    /**
     * Tenant activo y operativo.
     */
    ACTIVE,
    
    /**
     * Tenant suspendido temporalmente.
     */
    SUSPENDED,
    
    /**
     * Tenant cancelado por el usuario.
     */
    CANCELLED,
    
    /**
     * Tenant pendiente de activación.
     */
    PENDING,
    
    /**
     * Tenant en período de prueba.
     */
    TRIAL;
    
    /**
     * Verifica si el tenant puede operar normalmente.
     */
    public boolean isOperational() {
        return this == ACTIVE || this == TRIAL;
    }
    
    /**
     * Verifica si el tenant está inactivo.
     */
    public boolean isInactive() {
        return this == SUSPENDED || this == CANCELLED;
    }
}
