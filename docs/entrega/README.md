# Entrega final - Sistema de Reservas para Gimnasio

> **Fecha:** 2026-10-20 · **Verificaciones y evidencia generadas sobre el commit:** `26df59f`.
> Proyecto desarrollado con Spec-Driven Development: [`specs/`](../../specs/) es la fuente única de verdad.

## Estado del proyecto

| Aspecto | Estado |
| :--- | :--- |
| Historias de usuario | ✅ 5 de 5 completas (US01 a US05), con sus 24 criterios de aceptación. |
| Operaciones del contrato | ✅ 6 de 6 implementadas ([`gimnasio-openapi.yaml`](../../specs/api/gimnasio-openapi.yaml)). |
| Invariantes | ✅ INV-01 a INV-17 garantizadas donde indica el modelo de dominio. |
| Errores | ✅ Las 13 filas de ADR-0004 y el 500 genérico de ADR-0007. |
| Pruebas automáticas | ✅ 162 en verde (0 fallos, 0 errores, 0 omitidas). |
| Diferencias specs ↔ implementación | ✅ 7 detectadas y resueltas; 4 aceptadas en V1 ([`sync-report.md`](../sync-report.md)). |

Funcionalidad entregada:

| RF | Historia | Operación |
| :--- | :--- | :--- |
| RF-01 | US01 Registrar socio | `POST /api/v1/socios` |
| RF-02 | US02 Crear clase | `POST /api/v1/clases` |
| RF-03 | US03 Reservar plaza | `POST /api/v1/clases/{claseId}/reservas` |
| RF-04 | US04 Cancelar reserva | `POST /api/v1/reservas/{reservaId}/cancelar` |
| RF-05 | US05 Consultar reservas y disponibilidad | `GET /api/v1/socios/{socioId}/reservas`, `GET /api/v1/clases/{claseId}/disponibilidad` |

Lo que queda fuera de V1 está en [`vision.md`](../../specs/vision.md) (fuera de alcance) y en
[`PENDIENTES.md`](../../PENDIENTES.md).

## Cómo ejecutar

Requisitos: Java 25. Maven se incluye como wrapper.

```bash
./mvnw spring-boot:run
```

| Recurso | URL |
| :--- | :--- |
| API | `http://localhost:8080/api/v1/...` |
| Swagger UI (contrato autoritativo) | `http://localhost:8080/swagger-ui.html` |
| Consola H2 | `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:gimnasio`, usuario `sa`, sin contraseña) |

La base de datos es H2 en memoria: los datos se pierden al reiniciar.

## Verificaciones realizadas

| Verificación | Comando | Resultado | Evidencia |
| :--- | :--- | :--- | :--- |
| Pruebas automáticas | `./mvnw clean test` | ✅ 162 pruebas, 0 fallos | [`evidencia/surefire-resumen.md`](evidencia/surefire-resumen.md) |
| Coherencia de specs | `python3 tools/spec_lint.py` | ✅ OK: 6 operaciones, 0 errores | [`evidencia/spec_lint.txt`](evidencia/spec_lint.txt) |
| Specs ↔ código y pruebas | `python3 tools/spec_sync.py` | ✅ OK: 6 operaciones, 24 criterios, 110 métodos de prueba, 0 errores | [`evidencia/spec_sync.txt`](evidencia/spec_sync.txt) |
| Aplicación en ejecución | `npx newman run postman/gimnasio-reservas-flujo.postman_collection.json` | ✅ 35 peticiones, 65 aserciones, 0 fallos | [`sync-report.md`](../sync-report.md#ejecuciones) |

Las cuatro verificaciones se ejecutaron el 2026-10-20 sobre `26df59f`, durante la
[revisión final](../revisiones/2026-10-20-revision-final.md). Los archivos de `evidencia/` y la
[matriz de trazabilidad](matriz-trazabilidad.md) se generaron sobre el mismo commit. Los commits
posteriores solo cambian documentación y specs en prosa: el código, el contrato y los
scripts no cambian, así que estas verificaciones siguen siendo válidas (ver la
[actualización posterior](../revisiones/2026-10-20-revision-final.md#actualización-posterior-a-la-revisión)
de la revisión).

## Documentación

| Documento | Contenido |
| :--- | :--- |
| [`matriz-trazabilidad.md`](matriz-trazabilidad.md) | RF → US → CA → INV → operación → pruebas concretas. |
| [`docs/tecnica/`](../tecnica/README.md) | Arquitectura, API, dominio, decisiones, pruebas y una ficha por operación. |
| [`docs/sync-report.md`](../sync-report.md) | Diferencias specs ↔ implementación y resultados de las verificaciones. |
| [`docs/revisiones/2026-10-20-revision-final.md`](../revisiones/2026-10-20-revision-final.md) | Revisión final con criterio: verificaciones, hallazgos con el commit que los resolvió, pendientes y estado final de la V1. Ver su [actualización posterior](../revisiones/2026-10-20-revision-final.md#actualización-posterior-a-la-revisión) para los cambios posteriores a `26df59f`. |
| [`specs/CHANGELOG.md`](../../specs/CHANGELOG.md) | Registro de cambios de las specs: motivo, archivos, compatibilidad del contrato y commits de cada cambio. |
| [`specs/resources/plugins.md`](../../specs/resources/plugins.md) | Configuración del generador OpenAPI: plugin, parámetros del `pom.xml`, código generado y cómo regenerarlo. |
| [`specs/traceability.md`](../../specs/traceability.md) | Matriz de trazabilidad de la spec (fuente de la matriz de esta entrega). |
| [`tools/`](../../tools/README.md) | Scripts de validación `spec_lint.py`, `spec_sync.py` y `check_links.py`. |
| [`postman/`](../../postman/gimnasio-reservas-flujo.postman_collection.json) | Colección con el flujo completo y los casos de error. |
| [`prompts/`](../../prompts/README.md) | Prompts de IA: prompts reutilizables usados con Claude Code durante el desarrollo. |
