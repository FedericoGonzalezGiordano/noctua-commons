package com.noctua.commons.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.UUID;

/**
 * Interceptor que automáticamente resuelve y establece el tenant
 * para cada request HTTP. Se ejecuta antes de llegar al controller.
 */
@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {
    
    private final TenantResolver tenantResolver;
    
  
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Limpiar contexto anterior
        TenantContext.clear();
        
        UUID tenantId = null;
        
        // 1. Intentar resolver desde header (para comunicación entre microservicios)
        String tenantHeader = request.getHeader("X-Tenant-ID");
        if (tenantHeader != null) {
            Optional<UUID> headerTenant = tenantResolver.resolveFromHeader(tenantHeader);
            if (headerTenant.isPresent()) {
                tenantId = headerTenant.get();
            }
        }
        
        // 2. Si no hay header, resolver desde host/subdomain
        if (tenantId == null) {
            String host = request.getServerName();
            Optional<UUID> hostTenant = tenantResolver.resolveFromHost(host);
            if (hostTenant.isPresent()) {
                tenantId = hostTenant.get();
            }
        }
        
        // 3. Para desarrollo local, usar tenant por defecto
        if (tenantId == null && isLocalDevelopment(request)) {
            tenantId = getDefaultDevelopmentTenant();
        }
        
        // Establecer tenant en el contexto
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
            // También agregarlo como header para requests downstream
            response.setHeader("X-Current-Tenant-ID", tenantId.toString());
        }
        
        return true; // Continuar con el request
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        // Limpiar contexto al finalizar el request
        TenantContext.clear();
    }
    
    private boolean isLocalDevelopment(HttpServletRequest request) {
        String host = request.getServerName();
        return "localhost".equals(host) || host.startsWith("127.0.0.1") || host.startsWith("192");
    }
    
    private UUID getDefaultDevelopmentTenant() {
        // Tenant por defecto para desarrollo (el mismo que tienes en data-dev.sql)
        return UUID.fromString("770e8400-e29b-41d4-a716-446655440001");
    }
}
