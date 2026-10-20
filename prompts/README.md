# Prompts de IA

Esta carpeta reúne los prompts reutilizables que se usaron con Claude Code durante el desarrollo del
proyecto. Cada uno es una plantilla: explica cuándo usarlo, lista sus variables (`{{VARIABLE}}`) y
contiene el texto listo para copiar después de sustituirlas.

Todos siguen el flujo Spec-Driven Development del proyecto: leer primero las specs, detenerse si no
alcanzan, implementar capa por capa con una prueba por criterio de aceptación y no hacer commit sin
confirmación.

## Índice

| Prompt | Propósito | Variables |
| :--- | :--- | :--- |
| [`00-implementar-dominio.md`](00-implementar-dominio.md) | Implementar la capa de dominio de una historia: agregado, Value Objects, enums, excepciones e invariantes en Java puro, con pruebas `USxx_CAyy_`. | `{{HISTORIA}}`, `{{INVARIANTES}}` |
| [`01-implementar-operacion.md`](01-implementar-operacion.md) | Implementar una operación del contrato de punta a punta: puertos, Command/Query/Result, servicio, persistencia, web y pruebas por capa. | `{{OPERACION}}`, `{{HISTORIA}}` |
| [`02-refactorizar-desde-diseno.md`](02-refactorizar-desde-diseno.md) | Adaptar el código a un cambio de spec: análisis de impacto y refactor sin cambiar comportamiento, con `mvn test`. | `{{CAMBIO}}`, `{{SPEC_REF}}` |
| [`03-documentacion-ia.md`](03-documentacion-ia.md) | Generar o actualizar la documentación derivada de `docs/`, citando fuentes y anotando las diferencias en `sync-report.md`. | `{{SECCION}}` |

> **Nota:** `03-documentacion-ia.md` también cubre el mantenimiento de
> [`specs/CHANGELOG.md`](../specs/CHANGELOG.md) y la documentación de entrega
> ([`docs/entrega/`](../docs/entrega/README.md)): README, matriz de trazabilidad y evidencia, generadas
> a partir de las specs, el código y ejecuciones recientes de `mvn test`, `spec_lint.py` y `spec_sync.py`.

## Orden habitual

1. `00`: dominio de la historia.
2. `01`: cada operación de la historia, capa por capa.
3. `02`: cuando cambia una spec ya implementada.
4. `03`: al cerrar una historia, tras corregir specs o al preparar la entrega.

## Más contexto

- [`README.md`](../README.md) del proyecto: cómo ejecutar, arquitectura y uso de IA.
- [`CLAUDE.md`](../CLAUDE.md): fuentes de verdad y reglas obligatorias que los prompts dan por supuestas.
