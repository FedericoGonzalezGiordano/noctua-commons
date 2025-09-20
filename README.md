# Noctua Commons v1.0.0

Biblioteca compartida para funcionalidades comunes entre microservicios Noctua.

## 🎯 Propósito

Proporciona resolución automática de `tenantId` para arquitecturas multi-tenant, eliminando la necesidad de manejar manualmente el tenant en cada microservicio.

## 🚀 Características

- **TenantContext**: ThreadLocal para acceso al tenant actual
- **TenantResolver**: Resuelve tenant desde subdomain o headers HTTP
- **TenantInterceptor**: Intercepta requests automáticamente
- **TenantConfig**: Configuración Spring lista para usar

## 📦 Instalación

### 1. Clonar y compilar
```bash
git clone https://github.com/tu-org/noctua-commons.git
cd noctua-commons
mvn clean install
```

### 2. Agregar dependencia en tu microservicio
```xml
<dependency>
    <groupId>com.noctua</groupId>
    <artifactId>noctua-commons</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 3. Importar configuración
```java
@Import(TenantConfig.class)
@SpringBootApplication
public class YourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourServiceApplication.class, args);
    }
}
```

## 🔧 Uso

### Obtener tenant actual
```java
@RestController
public class YourController {
    
    @PostMapping("/api/something")
    public ResponseEntity<?> createSomething(@RequestBody SomeRequest request) {
        // El tenant se resuelve automáticamente
        UUID tenantId = TenantContext.getCurrentTenant();
        
        // Usar tenantId en tu lógica
        SomeEntity entity = SomeEntity.builder()
            .tenantId(tenantId)
            .data(request.getData())
            .build();
            
        return ResponseEntity.ok(service.save(entity));
    }
}
```

### Resolución automática
- **Desarrollo**: `localhost` → Usa tenant por defecto
- **Producción**: `empresa-abc.noctua.com` → Consulta tenancy-service por subdomain
- **APIs internas**: Header `X-Tenant-ID` → Usa UUID directamente

## 🏗️ Arquitectura

```
Request → TenantInterceptor → TenantResolver → TenantContext
                ↓                    ↓              ↓
        empresa-abc.noctua.com → Consulta Tenancy → UUID disponible
```

## ⚙️ Configuración

### Variables de entorno (opcional)
```properties
# URL del servicio de tenancy (por defecto: http://tenancy-service)
noctua.tenancy.service.url=http://tenancy-service

# Tenant por defecto para desarrollo
noctua.tenant.default.dev=770e8400-e29b-41d4-a716-446655440001
```

## 🔄 Versionado

- `1.0.0` - Versión inicial con resolución básica de tenant
- `1.1.0` - (Futuro) Caché de tenants, métricas
- `2.0.0` - (Futuro) Soporte para múltiples estrategias de resolución

## 🤝 Contribuir

1. Fork el repositorio
2. Crear feature branch: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -am 'Agregar nueva funcionalidad'`
4. Push branch: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

## 📄 Licencia

MIT License - ver archivo LICENSE para detalles.
