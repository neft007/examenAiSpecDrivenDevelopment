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

### Comandos

| Acción | Comando |
| :--- | :--- |
| Compilar y probar | `./mvnw clean test` |
| Ejecutar la aplicación | `./mvnw spring-boot:run` |

La aplicación arranca en `http://localhost:8080`.

### URLs de desarrollo

| Recurso | URL |
| :--- | :--- |
| Swagger UI (contrato autoritativo) | `http://localhost:8080/swagger-ui.html` |
| Consola H2 | `http://localhost:8080/h2-console` |

### Base de datos

H2 en memoria (ADR-0005), consola en `http://localhost:8080/h2-console`. Los datos se pierden al reiniciar.

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
│   └── out/persistence/ # Adaptador JPA (H2)
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
| [ADR-0005](specs/adr/0005-base-de-datos-h2.md) | H2 en memoria con Spring Data JPA | Aceptado |
| [ADR-0006](specs/adr/0006-spring-boot-4.md) | Spring Boot 4.1.x con Java 25 | Aceptado |
| [ADR-0007](specs/adr/0007-error-interno-generico.md) | Error interno genérico (500 INTERNAL_ERROR) | Aceptado |

## 🧪 Pruebas

- Un test por criterio de aceptación, con el nombre `USxx_CAyy_descripcionEnIngles`.
- Pruebas de dominio con JUnit puro (sin Spring).
- Pruebas de servicios con doble en memoria del puerto de salida.
- Pruebas de controladores con `@WebMvcTest` y casos de uso mockeados.
- Pruebas de persistencia con `@DataJpaTest` sobre H2.

Ejecutar todas las pruebas:

```bash
./mvnw clean test
```

### Colección de Postman

[`postman/gimnasio-reservas-flujo.postman_collection.json`](postman/gimnasio-reservas-flujo.postman_collection.json) (**Gimnasio Reservas - Flujo completo**) recorre el flujo completo de US01 a US05 y los casos de error de ADR-0004, con aserciones sobre el código HTTP y el body.

Requiere la aplicación levantada en `http://localhost:8080` (`./mvnw spring-boot:run`). Las peticiones dependen unas de otras, así que la colección se ejecuta completa y en orden:

- **Postman:** importar el archivo y ejecutarlo con el Runner.
- **newman:**

  ```bash
  npx newman run postman/gimnasio-reservas-flujo.postman_collection.json
  ```

DNI, email y fechas se generan en cada ejecución, de modo que se puede repetir sin reiniciar la aplicación.

> **Nota:** `MEMBRESIA_VENCIDA` (US03 CA-02) y `CLASE_YA_COMENZADA` (US03 CA-05) solo se cubren en las pruebas automáticas: en V1 no hay API para vencer una membresía ni para crear una clase en el pasado.

## 🛠️ Herramientas de validación

La carpeta [`tools/`](tools/) contiene dos scripts de Python 3 (requieren PyYAML) que complementan `./mvnw clean test`:

| Script | Qué valida |
| :--- | :--- |
| `spec_lint.py` | El contrato OpenAPI contra las specs: YAML y OpenAPI 3.x válidos, enums iguales a los catálogos, `operationId` y `x-spec-refs` en cada operación, `ErrorResponse` en las respuestas 4xx y `$ref` que resuelven. |
| `spec_sync.py` | Que el código y las pruebas sigan las specs: cada `operationId` implementado en un controlador, cada criterio de aceptación con una prueba `USxx_CAyy_`, la convención de nombres y las clases de prueba de `traceability.md`. |

Desde la raíz del repositorio (o con `python3` si no existe `python`):

```bash
python tools/spec_lint.py
python tools/spec_sync.py
```

Devuelven `0` si todo está bien y `1` si hay errores. El detalle de cada regla está en [`tools/README.md`](tools/README.md).

## 📚 Documentación

- [Visión del producto](specs/vision.md)
- [Requerimientos funcionales](specs/requirements/functional.md)
- [Historias de usuario](specs/user-stories/)
- [Ciclo de vida de las entidades](specs/domain/lifecycle.md)
- [Catálogos](specs/domain/catalogs.md)
- [Modelo de dominio](specs/architecture/domain-model.md)
- [Arquitectura](specs/architecture/architecture.md)
- [Stack tecnológico](specs/architecture/tech-stack.md)
- [Decisiones de arquitectura (ADRs)](specs/adr/)
- [Contrato OpenAPI](specs/api/gimnasio-openapi.yaml)
- [Configuración del generador OpenAPI](specs/resources/plugins.md): plugin `openapi-generator-maven-plugin` 7.22.0, parámetros del `pom.xml` y cómo regenerar las interfaces y los DTO.
- [Matriz de trazabilidad](specs/traceability.md)
- [Convenciones del proyecto](specs/conventions.md)
- [Registro de cambios de las specs](specs/CHANGELOG.md)
- [Tareas pendientes](PENDIENTES.md)
- [Documentación técnica](docs/tecnica/README.md): resumen derivado de las specs y el código (arquitectura, API, dominio, decisiones, pruebas y operaciones).
- [Entrega final](docs/entrega/README.md): estado del proyecto, verificaciones y evidencia.
- [Informe de sincronización](docs/sync-report.md): diferencias entre specs e implementación y su resolución.
- Revisión final: [`docs/revisiones/2026-10-20-revision-final.md`](docs/revisiones/2026-10-20-revision-final.md), actualizada hasta `86828a6` ([actualización posterior](docs/revisiones/2026-10-20-revision-final.md#actualización-posterior-a-la-revisión)).
- [Colección de Postman](postman/gimnasio-reservas-flujo.postman_collection.json): flujo completo de US01 a US05 y casos de error contra la aplicación en ejecución.
- [Herramientas de validación](tools/): `spec_lint.py` y `spec_sync.py`.
- [Prompts de IA](prompts/README.md): prompts reutilizables usados con Claude Code durante el desarrollo (dominio, operación, refactorización, documentación).

## 🤖 Uso de IA

Este proyecto se desarrolló con GitHub Copilot y Claude Code como asistentes, siguiendo el flujo de Spec-Driven Development:

1. Se escribió la especificación (visión, requerimientos, historias, criterios).
2. Se diseñó el modelo de dominio y el contrato OpenAPI.
3. La IA generó el código a partir de la spec.
4. Se revisó, corrigió y probó cada iteración.

La guía para la IA está en [`CLAUDE.md`](CLAUDE.md).

### Prompts de IA

La carpeta [`prompts/`](prompts/README.md) contiene los prompts reutilizables que se usaron con Claude Code. Cada uno indica cuándo usarlo y lleva variables (`{{HISTORIA}}`, `{{OPERACION}}`…) para aplicarlo a otra historia u operación:

| Prompt | Para qué sirve |
| :--- | :--- |
| [`00-implementar-dominio.md`](prompts/00-implementar-dominio.md) | Implementar la capa de dominio de una historia: agregado, Value Objects, enums, excepciones e invariantes, con sus pruebas `USxx_CAyy_`. |
| [`01-implementar-operacion.md`](prompts/01-implementar-operacion.md) | Implementar una operación del contrato de punta a punta: puertos, servicio, persistencia, web y pruebas por capa. |
| [`02-refactorizar-desde-diseno.md`](prompts/02-refactorizar-desde-diseno.md) | Adaptar el código a un cambio de spec: análisis de impacto y refactor sin cambiar comportamiento, con `mvn test`. |
| [`03-documentacion-ia.md`](prompts/03-documentacion-ia.md) | Generar o actualizar la documentación derivada de `docs/`, citando fuentes y anotando las diferencias en `sync-report.md`. También cubre la documentación de entrega (`docs/entrega/`): README, matriz de trazabilidad y evidencia. |

Estos prompts reflejan el flujo SDD seguido durante el desarrollo: primero se leen las specs, se detiene el trabajo si no alcanzan, se implementa capa por capa con una prueba por criterio de aceptación y la documentación se deriva de las specs y el código.

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