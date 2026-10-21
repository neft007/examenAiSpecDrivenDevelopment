# Herramientas de validación

Dos scripts que comprueban lo esencial del flujo SDD de este proyecto: que las specs sean coherentes
entre sí (`spec_lint.py`) y que la implementación siga a las specs (`spec_sync.py`). No sustituyen a
`./mvnw clean test`; lo complementan.

## Requisitos

- Python 3.8 o superior.
- PyYAML: `pip install pyyaml`.

## Ejecución

Desde la raíz del repositorio:

```bash
python tools/spec_lint.py
python tools/spec_sync.py
```

Si tu sistema solo tiene `python3`, usa `python3` en lugar de `python`. Los scripts localizan las
rutas a partir de su propia ubicación, así que también funcionan desde otro directorio.

| Código de salida | Significado |
| :---: | :--- |
| `0` | Sin errores (puede haber advertencias). |
| `1` | Al menos un error. |

La salida lista cada problema con su nivel y termina con una línea de resumen:

```text
spec_lint: specs/api/gimnasio-openapi.yaml, specs/domain/catalogs.md, specs/domain/lifecycle.md
  [ERROR] POST /api/v1/clases: falta x-spec-refs
Resultado: FALLO (6 operaciones; 1 errores, 0 advertencias)
```

- **`[ERROR]`**: rompe una regla del proyecto; el script devuelve 1.
- **`[WARN]`**: conviene revisarlo, pero no hace fallar el script.
- **`[INFO]`**: solo informa.

## spec_lint.py: coherencia de las specs

| # | Regla | Fuente |
| :---: | :--- | :--- |
| 1 | `specs/api/gimnasio-openapi.yaml` es YAML válido, declara `openapi: 3.x` y tiene `info` y `paths`. | — |
| 2 | Los enums `EstadoMembresia`, `EstadoClase` y `EstadoReserva` del contrato tienen los mismos valores **y en el mismo orden** que las tablas de `specs/domain/catalogs.md` y `specs/domain/lifecycle.md`. | `CLAUDE.md` regla 5 |
| 3 | Cada operación tiene `operationId` (sin repetir) y `x-spec-refs`, y cada RF o US citado existe en `specs/requirements/functional.md` o `specs/user-stories/`. Si el `operationId` no aparece en `specs/traceability.md`, se emite una advertencia. | `traceability.md` |
| 4 | Toda respuesta 4xx, directa o vía `components/responses`, tiene cuerpo `application/json` con el esquema `ErrorResponse`. | ADR-0004 |
| 5 | Todos los `$ref` del contrato son internos (`#/...`) y resuelven. | — |

## spec_sync.py: specs ↔ implementación

| # | Regla | Fuente |
| :---: | :--- | :--- |
| 1 | Cada `operationId` del contrato aparece como método en un `*Controller.java` de `adapter/in/web`. Si aparece en más de un controlador, se emite una advertencia. | `CLAUDE.md` regla 2 |
| 2 | Cada criterio `**CA-yy:**` de `specs/user-stories/USxx-*.md` tiene al menos un método de prueba que empieza por `USxx_CAyy_`. | `CLAUDE.md` regla 7 |
| 3 | Todo método de prueba que empieza por `US` sigue `USxx_CAyy_descripcionEnIngles` (descripción en camelCase) y cita un criterio existente. | `CLAUDE.md` regla 7 |
| 4 | Cada clase de prueba citada en `specs/traceability.md` (nombres terminados en `Test` o `Tests`) existe en `src/test/java`. | `traceability.md` |

Solo se consideran métodos de prueba los anotados con `@Test`, `@ParameterizedTest`, `@RepeatedTest`
o `@TestFactory`. Las pruebas **sin** prefijo `USxx_CAyy_` (recurso inexistente, `restore`, reglas
transversales como `GlobalExceptionHandlerTest`, arranque del contexto) están permitidas porque no cubren
un criterio concreto. Se cuentan en una línea `[INFO]`.

## Limitaciones

Son validaciones textuales, pensadas para este proyecto:

- `spec_sync.py` busca el nombre del `operationId` en el código fuente; no comprueba la firma ni que el
  controlador implemente la interfaz generada. Eso lo garantiza el compilador: con `skipDefaultInterface`
  los métodos de `SociosApi`, `ClasesApi` y `ReservasApi` no tienen implementación por defecto
  (configuración del generador en [`specs/resources/plugins.md`](../specs/resources/plugins.md)).
- Los criterios se leen con el formato `**CA-01:**` de las historias actuales.
- No validan el contrato contra el esquema oficial de OpenAPI.
