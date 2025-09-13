package com.noctua.commons.services;

import com.noctua.commons.dto.TenantDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache local para tenants con TTL para evitar llamadas HTTP en cada request.
 * Mejor práctica para interceptors síncronos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantCacheService {
    
    private final TenantHttpService tenantHttpService;
    private final Map<String, CacheEntry> subdomainCache = new ConcurrentHashMap<>();
    private final Map<UUID, CacheEntry> tenantIdCache = new ConcurrentHashMap<>();

    private static final int CACHE_TTL_MINUTES = 5;

    /**
     * Obtiene tenant por subdomain con cache local.
     */
    public Optional<UUID> getTenantIdBySubdomain(String subdomain) {
        if (subdomain == null || subdomain.isEmpty()) {
            return Optional.empty();
        }

        // 1. Verificar cache
        CacheEntry cached = subdomainCache.get(subdomain);
        if (cached != null && !cached.isExpired()) {
            log.debug("Cache hit for subdomain: {}", subdomain);
            return cached.getTenantId();
        }
        
        // 2. Cache miss - fetch asíncrono en background
        fetchTenantAsync(subdomain);
        
        // 3. Retornar cached value si existe (aunque esté expirado)
        return cached != null ? cached.getTenantId() : Optional.empty();
    }
    
    /**
     * Pre-carga tenant de forma asíncrona.
     */
    private void fetchTenantAsync(String subdomain) {
        tenantHttpService.getTenantBySubdomain(subdomain)
                .subscribe(
                    tenant -> {
                        if (tenant.isValid()) {
                            CacheEntry entry = new CacheEntry(Optional.of(tenant.id()));
                            subdomainCache.put(subdomain, entry);
                            tenantIdCache.put(tenant.id(), entry);
                            log.debug("Cached tenant q {} for subdomain {}", tenant.id(), subdomain);
                        } else {
                            log.debug("Cached tenant t {} for subdomain {}", tenant.id(), subdomain);
                            // Cache negative result
                            subdomainCache.put(subdomain, new CacheEntry(Optional.empty()));
                        }
                    },
                    error -> {
                        log.warn("Error fetching tenant for subdomain {}: {}", subdomain, error.getMessage());
                        // Keep old cache entry if exists
                    }
                );
    }
    
    /**
     * Verifica si un tenant está activo (con cache).
     */
    public boolean isTenantActive(UUID tenantId) {
        if (tenantId == null) return false;
        
        CacheEntry cached = tenantIdCache.get(tenantId);
        if (cached != null && !cached.isExpired()) {
            return cached.getTenantId().isPresent();
        }
        
        // Fetch async y retornar cached si existe
        tenantHttpService.isTenantActive(tenantId)
                .subscribe(isActive -> {
                    CacheEntry entry = new CacheEntry(isActive ? Optional.of(tenantId) : Optional.empty());
                    tenantIdCache.put(tenantId, entry);
                });
        
        return cached != null && cached.getTenantId().isPresent();
    }
    
    /**
     * Limpia cache expirado periódicamente.
     */
    public void cleanExpiredEntries() {
        subdomainCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        tenantIdCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Entry del cache con TTL.
     */
    private static class CacheEntry {
        private final Optional<UUID> tenantId;
        private final LocalDateTime createdAt;

        public CacheEntry(Optional<UUID> tenantId) {
            this.tenantId = tenantId;
            this.createdAt = LocalDateTime.now();
        }

        public Optional<UUID> getTenantId() {
            return tenantId;
        }

        public boolean isExpired() {
            return createdAt.plusMinutes(CACHE_TTL_MINUTES).isBefore(LocalDateTime.now());
        }
    }
}
