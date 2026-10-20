# Documentación técnica

> **Documentación derivada.** Resume las specs y el código; no los sustituye. Si hay diferencias,
> manda [`specs/`](../../specs/), que es la fuente única de verdad, y la diferencia se anota en
> [`sync-report.md`](../sync-report.md).

## Índice

| Documento | Contenido | Se deriva de |
| :--- | :--- | :--- |
| [arquitectura.md](arquitectura.md) | Capas, regla de dependencia, puertos y paquetes. | [`architecture.md`](../../specs/architecture/architecture.md), [`tech-stack.md`](../../specs/architecture/tech-stack.md), ADR-0001 |
| [api.md](api.md) | Endpoints, esquemas y respuestas de error. | [`gimnasio-openapi.yaml`](../../specs/api/gimnasio-openapi.yaml), ADR-0004, ADR-0007 |
| [dominio.md](dominio.md) | Glosario, estados e invariantes. | [`domain-model.md`](../../specs/architecture/domain-model.md), [`lifecycle.md`](../../specs/domain/lifecycle.md), [`catalogs.md`](../../specs/domain/catalogs.md) |
| [decisiones.md](decisiones.md) | Resumen de los ADR. | [`specs/adr/`](../../specs/adr/) |
| [pruebas.md](pruebas.md) | Estrategia de pruebas, cobertura por criterio de aceptación y colección de Postman. | [`traceability.md`](../../specs/traceability.md), `src/test/java`, `postman/` |

### Operaciones

Una ficha por operación del contrato: entrada, flujo, reglas, errores y pruebas.

| Ficha | operationId | Historia |
| :--- | :--- | :--- |
| [registrarSocio.md](operaciones/registrarSocio.md) | `registrarSocio` | US01 |
| [crearClase.md](operaciones/crearClase.md) | `crearClase` | US02 |
| [reservarPlaza.md](operaciones/reservarPlaza.md) | `reservarPlaza` | US03 |
| [cancelarReserva.md](operaciones/cancelarReserva.md) | `cancelarReserva` | US04 |
| [consultarReservas.md](operaciones/consultarReservas.md) | `consultarReservas`, `consultarDisponibilidad` | US05 |

`consultarDisponibilidad` se documenta en la ficha de `consultarReservas` porque las dos pertenecen a US05.

### Sincronización

- [`../sync-report.md`](../sync-report.md): diferencias detectadas entre specs e implementación.

### Colección de Postman

- [`postman/gimnasio-reservas-flujo.postman_collection.json`](../../postman/gimnasio-reservas-flujo.postman_collection.json):
  flujo completo de US01 a US05 y los casos de error de ADR-0004 contra la aplicación en ejecución.
  Cómo ejecutarla y qué cubre: [pruebas.md](pruebas.md#colección-de-postman).

### Herramientas de validación

- [`tools/`](../../tools/): scripts de Python 3 (con PyYAML) que se ejecutan desde la raíz y devuelven 1 si hay errores.
  - `spec_lint.py`: valida el contrato OpenAPI contra las specs (enums, `operationId`, `x-spec-refs`,
    `ErrorResponse` en las 4xx y `$ref`).
  - `spec_sync.py`: verifica que el código y las pruebas sigan las specs (operaciones implementadas,
    una prueba por criterio de aceptación, convención de nombres y clases de `traceability.md`).

  Detalle de las reglas en [`tools/README.md`](../../tools/README.md).

### Prompts de IA

- [`prompts/`](../../prompts/): prompts reutilizables usados con Claude Code durante el desarrollo
  (dominio, operación, refactorización, documentación). Cada prompt indica cuándo usarlo y las
  variables que acepta (`{{HISTORIA}}`, `{{OPERACION}}`…).

## Cómo se mantiene

1. Primero cambia la spec (`specs/`), después el código y por último esta documentación.
2. Cada documento indica en su cabecera las fuentes de las que se deriva.
3. Si al actualizar se detecta una diferencia entre spec y código, se anota en `sync-report.md`
   en lugar de "corregirla" aquí.
