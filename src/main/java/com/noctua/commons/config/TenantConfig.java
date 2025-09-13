package com.noctua.commons.config;

import com.noctua.commons.tenant.TenantInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
}