package com.noctua.commons.config;

import com.noctua.commons.services.TenantCacheService;
import com.noctua.commons.services.TenantHttpService;
import com.noctua.commons.tenant.TenantInterceptor;
import com.noctua.commons.tenant.TenantResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Auto-configuración principal de Noctua Commons.
 * Registra automáticamente todos los beans necesarios.
 */
@Configuration
public class NoctuaCommonsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantHttpService tenantHttpService(WebClient webClient) {
        return new TenantHttpService(webClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantCacheService tenantCacheService(TenantHttpService tenantHttpService) {
        return new TenantCacheService(tenantHttpService);
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantResolver tenantResolver(TenantCacheService tenantCacheService) {
        return new TenantResolver(tenantCacheService);
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantInterceptor tenantInterceptor(TenantResolver tenantResolver) {
        return new TenantInterceptor(tenantResolver);
    }
}