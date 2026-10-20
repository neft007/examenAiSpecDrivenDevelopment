# Decisiones de arquitectura

> **Fuentes:** [`specs/adr/`](../../specs/adr/) (índice en [`specs/adr/README.md`](../../specs/adr/README.md)).
> Este documento resume; el texto vigente de cada decisión es el del ADR.

| ADR | Decisión | Por qué | Consecuencia en el código |
| :--- | :--- | :--- | :--- |
| [0001](../../specs/adr/0001-arquitectura-hexagonal.md) | Arquitectura hexagonal con puertos in/out y un modelo por frontera. | Probar las reglas sin servidor ni BD y aislar los cambios de contrato o persistencia. | Paquetes `domain`, `application.port.in/out`, `application.service`, `adapter.in.web`, `adapter.out.persistence`. Más clases y mapeos (Request → Command → dominio → Result). |
| [0002](../../specs/adr/0002-transiciones-como-acciones.md) | Las transiciones de estado son `POST` a un sub-recurso con verbo de negocio. | Cada transición tiene reglas y errores propios; un `PATCH` del estado permitiría saltarlas. | `POST /api/v1/reservas/{reservaId}/cancelar` → `Reserva.cancelar()`. No hay `PUT`/`PATCH`. |
| [0003](../../specs/adr/0003-identificador-uuid.md) | Identificador técnico UUID generado por el sistema. | El DNI podría cambiar y un id autoincremental revela volumen. | `SocioId`, `ClaseId`, `ReservaId` se crean en el dominio; las rutas usan UUID. |
| [0004](../../specs/adr/0004-codigos-de-error.md) | HTTP según el tipo de regla incumplida: 400 datos, 404 inexistente, 409 conflicto o regla de negocio. | El cliente necesita distinguir qué falló. | Una excepción por fila; `GlobalExceptionHandler` mapea cada una a su HTTP y `code`. |
| [0005](../../specs/adr/0005-base-de-datos-h2.md) | H2 en memoria con Spring Data JPA; consola H2 solo en desarrollo. | Ejecutar sin infraestructura y aun así ejercitar un adaptador real y restricciones únicas. | `jdbc:h2:mem:gimnasio`, `create-drop`, `/h2-console`. Los datos se pierden al reiniciar. |
| [0006](../../specs/adr/0006-spring-boot-4.md) | Spring Boot 4.1.x con Java 25. | Initializr ya no ofrece 3.x. | Módulos separados de Boot 4 en el `pom.xml` (`*-test`, `spring-boot-h2console`); generador con `useSpringBoot4` y `useJackson3`. |
| [0007](../../specs/adr/0007-error-interno-generico.md) | Toda excepción no mapeada responde `500 INTERNAL_ERROR` con mensaje genérico y `details` vacío. | No filtrar detalles internos y mantener siempre el formato `ErrorResponse`. | `handleUnexpected` registra la excepción con nivel `ERROR` y responde `"Unexpected error"`. |

Todos los ADR están en estado **Aceptado**.

## Decisiones de modelado relacionadas

No son ADR, pero condicionan la implementación ([`domain-model.md` §4](../../specs/architecture/domain-model.md#4-decisiones-de-modelado)):

- La membresía es un enum (catálogo cerrado), no un Value Object.
- Cambiar la membresía queda fuera de V1 (también en [`vision.md`](../../specs/vision.md)).
- Las unicidades y las reglas de reserva viven en los servicios porque requieren conocer otros agregados.
- El email usa la longitud por defecto de JPA (255) porque ninguna spec define un máximo.

## Cómo se añade una decisión

Nuevo archivo `specs/adr/NNNN-titulo.md` con contexto, decisión, alternativas y consecuencias; se
registra en `specs/adr/README.md` y se hace commit con prefijo `design:`. Añadir o cambiar una
tecnología del stack también requiere un ADR ([`tech-stack.md`](../../specs/architecture/tech-stack.md)).
