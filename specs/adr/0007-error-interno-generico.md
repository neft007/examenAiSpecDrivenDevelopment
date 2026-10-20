# ADR-0007: Error interno genérico (500 INTERNAL_ERROR)

- **Estado:** Aceptado
- **Fecha:** 2026-10-20
- **Specs relacionadas:** ADR-0004, `api/gimnasio-openapi.yaml`

## Contexto

ADR-0004 fija el código `500 INTERNAL_ERROR` para errores inesperados, pero no especifica qué puede contener la respuesta (mensaje, detalles, traza). Sin una decisión, Spring responde con su formato por defecto, distinto de `ErrorResponse`, que además puede exponer detalles internos.

## Decisión

- Toda excepción no mapeada por ADR-0004 responde `500` con `ErrorResponse { code: INTERNAL_ERROR, message: "Unexpected error", details: [] }`.
- La respuesta nunca incluye el mensaje de la excepción ni el stack trace; la excepción completa se registra en el log con nivel `ERROR`.

## Alternativas consideradas

| Alternativa | A favor | En contra |
| :--- | :--- | :--- |
| Respuesta por defecto de Spring | Sin código adicional. | Formato distinto de `ErrorResponse`; puede exponer detalles internos. |
| 500 con el mensaje de la excepción | Facilita depurar. | Filtra información interna al cliente. |

## Consecuencias

- El cliente recibe siempre `ErrorResponse`, también ante fallos inesperados.
- El diagnóstico se hace con el log, no con la respuesta.
- Verificación: una prueba web provoca una excepción no prevista y comprueba HTTP 500, `code` y que el mensaje interno no aparece en la respuesta.