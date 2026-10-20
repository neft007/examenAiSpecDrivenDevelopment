# ADR-0004: Códigos HTTP de error según el tipo de regla incumplida

- **Estado:** Aceptado
- **Fecha:** 2026-10-18
- **Specs relacionadas:** US01–US05, invariantes INV-01 a INV-15

## Contexto

Los criterios de aceptación dicen "se rechaza" sin indicar cómo. El cliente necesita distinguir entre error de datos, recurso inexistente, conflicto de estado y regla de negocio no cumplida.

## Decisión

| HTTP | Código de error | Cuándo | Invariantes |
| :--- | :--- | :--- | :--- |
| 400 | `VALIDATION_ERROR` | Formato, longitud, rango, valor fuera de catálogo. | INV-01, INV-04, INV-05, INV-06, INV-07, INV-08, INV-16, INV-17 |
| 404 | `SOCIO_NOT_FOUND` | El socio no existe. | — |
| 404 | `CLASE_NOT_FOUND` | La clase no existe. | — |
| 404 | `RESERVA_NOT_FOUND` | La reserva no existe. | — |
| 409 | `DNI_DUPLICADO` | El DNI ya existe. | INV-02 |
| 409 | `EMAIL_DUPLICADO` | El email ya existe. | INV-03 |
| 409 | `MEMBRESIA_VENCIDA` | La membresía del socio está vencida. | INV-09 |
| 409 | `CLASE_YA_COMENZADA` | La clase ya comenzó. | INV-10 |
| 409 | `AFORO_COMPLETO` | No hay plazas disponibles. | INV-11 |
| 409 | `RESERVA_SOLAPADA` | El socio ya tiene una reserva en ese horario. | INV-12 |
| 409 | `INVALID_STATE_TRANSITION` | Transición no permitida. | INV-13 |
| 409 | `CANCELACION_FUERA_DE_PLAZO` | Menos de 2 horas de antelación. | INV-14 |
| 500 | `INTERNAL_ERROR` | Error inesperado, no mapeado. | — |

Todas las respuestas de error usan el mismo esquema `ErrorResponse { code, message, details }`.

## Alternativas consideradas

| Alternativa | A favor | En contra |
| :--- | :--- | :--- |
| Todo como 400 | Simple. | El cliente no distingue datos inválidos de conflictos de estado. |
| Un solo código 409 para todos los conflictos | Menos códigos. | El cliente no puede reaccionar de forma específica (ej. reintentar si es aforo, no si es membresía vencida). |

## Consecuencias

- Cada fila tiene una excepción propia en `domain.exception`.
- El `GlobalExceptionHandler` mapea cada excepción de dominio a exactamente una fila de esta tabla.
- Las pruebas verifican tanto el HTTP como el `code`.