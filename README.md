# Sistema de Reservas para Gimnasio

API REST para la gestión de reservas de clases en un gimnasio, desarrollada con **Spec-Driven Development (SDD)**, **Arquitectura Hexagonal** y **Spring Boot 4**.

## 📋 Descripción

Este proyecto permite a los socios de un gimnasio reservar plazas en clases, cancelar reservas y consultar su disponibilidad. El sistema garantiza que:
- Solo los socios con membresía activa puedan reservar.
- No se supere el aforo máximo de cada clase.
- Un socio no tenga dos reservas en el mismo horario.
- Las cancelaciones se realicen con al menos 2 horas de antelación.

La carpeta [`specs/`](specs/) es la **fuente única de verdad**: nada se implementa si no está especificado primero.

## 🚀 Cómo ejecutar

### Requisitos
- Java 25 (o superior)
- Maven (incluido como wrapper `./mvnw`)
- Docker (opcional, para base de datos PostgreSQL/MySQL)

### Comandos

| Acción | Comando |
| :--- | :--- |
| Compilar y probar | `./mvnw clean test` |
| Ejecutar la aplicación | `./mvnw spring-boot:run` |
| Validar specs (si está disponible) | `python tools/spec_lint.py` |
| Verificar specs ↔ implementación | `python tools/spec_sync.py` |

La aplicación arranca en `http://localhost:8080`.

### Base de datos

Por defecto, se usa **H2 en memoria** (los datos se pierden al reiniciar). Para usar PostgreSQL, levanta el contenedor con Docker:

```bash
docker-compose up -d
```

Y configura las credenciales en `src/main/resources/application.properties`.

## 🏗️ Arquitectura

Monolito con **Arquitectura Hexagonal** (puertos y adaptadores):

```
src/main/java/.../
├── domain/              # Modelo y reglas de negocio (Java puro)
├── application/
│   ├── port/in/         # Casos de uso (interfaces) + Commands/Queries/Results
│   ├── port/out/        # Puertos de persistencia
│   └── service/         # Implementación de los casos de uso
├── adapter/
│   ├── in/web/          # Controladores REST (implementan el contrato OpenAPI)
│   └── out/persistence/ # Adaptador JPA (H2/PostgreSQL)
└── config/              # Configuración (Clock, etc.)
```

**Regla clave:** el dominio nunca depende de Spring, JPA ni HTTP. El controlador recibe `*Result`, nunca entidades de dominio.

## 📦 Funcionalidades (V1)

| RF | Historia | Endpoint | Estado |
| :--- | :--- | :--- | :---: |
| RF-01 | Registrar socio | `POST /api/v1/socios` | ✅ |
| RF-02 | Crear clase | `POST /api/v1/clases` | ✅ |
| RF-03 | Reservar plaza | `POST /api/v1/clases/{claseId}/reservas` | ✅ |
| RF-04 | Cancelar reserva | `POST /api/v1/reservas/{reservaId}/cancelar` | ✅ |
| RF-05 | Consultar reservas y disponibilidad | `GET /api/v1/socios/{socioId}/reservas`<br>`GET /api/v1/clases/{claseId}/disponibilidad` | ✅ |

## 📐 Decisiones de Arquitectura (ADRs)

| ADR | Decisión | Estado |
| :--- | :--- | :--- |
| [ADR-0001](specs/adr/0001-arquitectura-hexagonal.md) | Arquitectura hexagonal con puertos in/out | Aceptado |
| [ADR-0002](specs/adr/0002-transiciones-como-acciones.md) | Transiciones de estado como acciones POST | Aceptado |
| [ADR-0003](specs/adr/0003-identificador-uuid.md) | Identificador técnico UUID | Aceptado |
| [ADR-0004](specs/adr/0004-codigos-de-error.md) | Códigos HTTP de error según tipo de regla | Aceptado |
| [ADR-0005](specs/adr/0005-base-de-datos-h2.md) | H2 en memoria para desarrollo, PostgreSQL para producción | Aceptado |
| [ADR-0006](specs/adr/0006-spring-boot-4.md) | Spring Boot 4.1.x con Java 25 | Aceptado |

## 🧪 Pruebas

- Un test por criterio de aceptación, con el nombre `USxx_CAyy_descripcion`.
- Pruebas de dominio con JUnit puro (sin Spring).
- Pruebas de servicios con doble en memoria del puerto de salida.
- Pruebas de controladores con `@WebMvcTest` y casos de uso mockeados.
- Pruebas de persistencia con `@DataJpaTest` sobre H2.

Ejecutar todas las pruebas:

```bash
./mvnw clean test
```

## 📚 Documentación

- [Visión del producto](specs/vision.md)
- [Requerimientos funcionales](specs/requirements/functional.md)
- [Historias de usuario](specs/user-stories/)
- [Modelo de dominio](specs/architecture/domain-model.md)
- [Contrato OpenAPI](specs/api/gimnasio-openapi.yaml)
- [Matriz de trazabilidad](specs/traceability.md)

## 🤖 Uso de IA

Este proyecto se desarrolló con GitHub Copilot y Claude Code como asistentes, siguiendo el flujo de Spec-Driven Development:

1. Se escribió la especificación (visión, requerimientos, historias, criterios).
2. Se diseñó el modelo de dominio y el contrato OpenAPI.
3. La IA generó el código a partir de la spec.
4. Se revisó, corrigió y probó cada iteración.

La guía para la IA está en [`CLAUDE.md`](CLAUDE.md).

## 📝 Convenciones de commits

| Prefijo | Uso |
| :--- | :--- |
| `spec:` | Cambios en la especificación |
| `design:` | Cambios en el diseño (modelo, ADRs) |
| `contract:` | Cambios en el contrato OpenAPI |
| `feat:` | Nueva funcionalidad |
| `test:` | Nuevas pruebas |
| `fix:` | Corrección de errores |
| `refactor:` | Refactorización sin cambio de comportamiento |
| `docs:` | Documentación |
| `tooling:` | Herramientas y configuración |

## 📄 Licencia

Proyecto académico desarrollado para el curso **AI-Spec Driven Development with Java** de Galaxy Training.