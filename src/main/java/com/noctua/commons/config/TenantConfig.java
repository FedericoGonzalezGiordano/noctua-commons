package com.noctua.commons.config;

import com.noctua.commons.tenant.TenantInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración común para el manejo de tenants.
 * Debe ser importada en cada microservicio que use multi-tenancy.
 */
@Configuration
@RequiredArgsConstructor
public class TenantConfig implements WebMvcConfigurer {
    
    private final TenantInterceptor tenantInterceptor;
    
    
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**") // Solo interceptar APIs
                .excludePathPatterns(
                    "/api/health/**",
                    "/api/actuator/**",
                    "/h2-console/**"
                );
    }
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }
}
