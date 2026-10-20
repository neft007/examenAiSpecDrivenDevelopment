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
| [pruebas.md](pruebas.md) | Estrategia de pruebas y cobertura por criterio de aceptación. | [`traceability.md`](../../specs/traceability.md), `src/test/java` |

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

## Cómo se mantiene

1. Primero cambia la spec (`specs/`), después el código y por último esta documentación.
2. Cada documento indica en su cabecera las fuentes de las que se deriva.
3. Si al actualizar se detecta una diferencia entre spec y código, se anota en `sync-report.md`
   en lugar de "corregirla" aquí.
