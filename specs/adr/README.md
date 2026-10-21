# Decisiones de Arquitectura

| ADR | Decisión | Estado |
| :--- | :--- | :--- |
| [0001](0001-arquitectura-hexagonal.md) | Hexagonal con puertos in/out y modelos por frontera | Aceptado |
| [0002](0002-transiciones-como-acciones.md) | Transiciones como acciones POST | Aceptado |
| [0003](0003-identificador-uuid.md) | UUID técnico | Aceptado |
| [0004](0004-codigos-de-error.md) | Códigos HTTP según tipo de regla | Aceptado |
| [0005](0005-base-de-datos-h2.md) | H2 en memoria con Spring Data JPA | Aceptado |
| [0006](0006-spring-boot-4.md) | Spring Boot actualizado | Aceptado |
| [0007](0007-error-interno-generico.md) | Error interno genérico sin detalles internos | Aceptado |

La configuración del generador OpenAPI no es un ADR: está documentada en
[`resources/plugins.md`](../resources/plugins.md) y concreta para la generación de la API lo decidido en
ADR-0006 (Spring Boot 4: `useSpringBoot4`, `useJackson3`).
