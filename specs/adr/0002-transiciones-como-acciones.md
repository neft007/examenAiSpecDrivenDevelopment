# ADR-0002: Transiciones de estado como acciones (`POST /reservas/{id}/cancelar`)

- **Estado:** Aceptado
- **Fecha:** 2026-10-18
- **Specs relacionadas:** `domain/lifecycle.md`, US04

## Contexto

La reserva cambia de estado por acciones de negocio con reglas distintas: cancelar exige al menos 2 horas de antelación. El estado de una clase también cambia por acciones (cancelar, completar).

## Decisión

Cada transición es un `POST` a un sub-recurso con verbo de negocio:

- `POST /api/v1/reservas/{id}/cancelar`
- `POST /api/v1/clases/{id}/cancelar` (si se implementa en el futuro)

El estado nunca se modifica directamente en `PUT`/`PATCH` de la entidad.

## Alternativas consideradas

| Alternativa | A favor | En contra |
| :--- | :--- | :--- |
| `PATCH /reservas/{id}` con `{ "estado": "CANCELADA" }` | Un solo endpoint. | Cada transición exige datos distintos; validación condicional difícil de expresar en OpenAPI; permite saltarse reglas. |
| `PUT /reservas/{id}` con el estado completo | Semántica de reemplazo. | Expone el estado como un campo editable, lo que permite transiciones inválidas. |

## Consecuencias

- Cada acción tiene su propio `operationId`, request y errores, y mapea 1:1 a un caso de uso.
- La actualización de datos (si existiera) nunca podría cambiar el estado.
- Las reglas de transición viven en el dominio (`Reserva.cancelar()`, `Clase.cancelar()`), no en el controlador.