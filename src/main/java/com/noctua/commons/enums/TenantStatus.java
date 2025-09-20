package com.noctua.commons.enums;

/**
 * Estados posibles de un tenant en el sistema.
 * Debe coincidir con TenantStatus del tenancy-service.
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
    CANCELLED;
    
    /**
     * Verifica si el tenant puede operar normalmente.
     */
    public boolean isOperational() {
        return this == ACTIVE;
    }
    
    /**
     * Verifica si el tenant está inactivo.
     */
    public boolean isInactive() {
        return this == SUSPENDED || this == CANCELLED;
    }
}
