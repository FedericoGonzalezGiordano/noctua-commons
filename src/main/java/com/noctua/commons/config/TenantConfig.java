package com.noctua.commons.config;

import com.noctua.commons.tenant.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración común para el manejo de tenants.
 * Debe ser importada en cada microservicio que use multi-tenancy.
 */
@Configuration
public class TenantConfig implements WebMvcConfigurer {
    
    private final TenantInterceptor tenantInterceptor;
    
    @Autowired
    public TenantConfig(TenantInterceptor tenantInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
    }
    
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
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
