# Convenciones del Proyecto

## Idioma
- **Castellano:** visión, requerimientos, historias de usuario, ADR y descripciones del contrato.
- **Inglés:** todo lo que se convierte en código: rutas, operationId, campos, enums, clases, métodos y pruebas.
  Las rutas, `operationId`, campos y enums del contrato se convierten en código mediante el generador OpenAPI
  (configuración en [`resources/plugins.md`](resources/plugins.md)).

## Códigos de dominio
- Estados y catálogos se escriben en inglés, MAYÚSCULAS_CON_GUION_BAJO (ej. `ACTIVA`, `PROGRAMADA`).
- Cada código tiene su equivalente en castellano documentado en `specs/domain/`.
- Ningún código puede usarse en el contrato o en el código si no existe primero en `specs/domain/`.
