package com.noctua.commons.config;

import com.noctua.commons.services.TenantCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuración para limpieza automática del cache de tenants.
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CacheConfig {
    
    private final TenantCacheService tenantCacheService;
    
    /**
     * Limpia entradas expiradas del cache cada 10 minutos.
     */
    @Scheduled(fixedRate = 600000) // 10 minutos
    public void cleanExpiredCacheEntries() {
        tenantCacheService.cleanExpiredEntries();
    }
}
