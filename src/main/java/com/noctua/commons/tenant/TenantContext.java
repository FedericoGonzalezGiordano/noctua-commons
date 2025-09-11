package com.noctua.commons.tenant;

import java.util.UUID;

/**
 * Thread-local context para almacenar información del tenant actual.
 * Permite acceso al tenantId desde cualquier parte de la aplicación.
 */
public class TenantContext {
    
    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();
    
    /**
     * Establece el tenant actual para el hilo de ejecución.
     */
    public static void setCurrentTenant(UUID tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    
    /**
     * Obtiene el tenant actual del hilo de ejecución.
     */
    public static UUID getCurrentTenant() {
        return CURRENT_TENANT.get();
    }
    
    /**
     * Limpia el tenant del hilo de ejecución.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
    
    /**
     * Verifica si hay un tenant establecido.
     */
    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}
