# Revisión final con criterio · 2026-10-20

> **Fecha:** 2026-10-20. **Commit revisado:** `26df59f` (código sin cambios desde `065cb8e`).
> **Fuentes:** [`specs/`](../../specs/), [`specs/CHANGELOG.md`](../../specs/CHANGELOG.md),
> [`docs/sync-report.md`](../sync-report.md), [`PENDIENTES.md`](../../PENDIENTES.md) y `git log`.

## Introducción

Una **revisión con criterio** no se limita a comprobar que las pruebas pasan. También mira si lo que
dicen las specs y lo que hace el código siguen siendo coherentes, si cada decisión está justificada y
registrada, y qué queda abierto conscientemente. Combina verificaciones automáticas con una lectura
humana de los hallazgos acumulados durante el desarrollo.

Esta revisión se hizo el 2026-10-20, al cerrar la V1 (US01 a US05) y preparar la entrega. Las
verificaciones automáticas se volvieron a ejecutar ese día sobre el commit `26df59f`.

## Verificaciones automáticas

| Verificación | Comando | Resultado |
| :--- | :--- | :--- |
| Pruebas automáticas | `./mvnw clean test` | ✅ 162 pruebas, 0 fallos, 0 errores, 0 omitidas. |
| Coherencia de specs | `python3 tools/spec_lint.py` | ✅ OK: 6 operaciones, 0 errores, 0 advertencias. |
| Specs ↔ código y pruebas | `python3 tools/spec_sync.py` | ✅ OK: 6 operaciones en 3 controladores, 24 criterios, 110 métodos de prueba, 21 clases de `traceability.md`, 0 errores. 22 pruebas sin prefijo `USxx_CAyy_` (admitidas). |
| Aplicación en ejecución | `npx newman run postman/gimnasio-reservas-flujo.postman_collection.json` | ✅ 35 peticiones, 65 aserciones, 0 fallos, con la aplicación recién arrancada (`./mvnw spring-boot:run`). |

La evidencia de estas ejecuciones se regeneró sobre el mismo commit, `26df59f`:

- [`surefire-resumen.md`](../entrega/evidencia/surefire-resumen.md): pruebas por capa y por clase.
- [`spec_lint.txt`](../entrega/evidencia/spec_lint.txt): salida de `spec_lint.py`.
- [`spec_sync.txt`](../entrega/evidencia/spec_sync.txt): salida de `spec_sync.py`.

La [matriz de trazabilidad](../entrega/matriz-trazabilidad.md) de la entrega también se regeneró sobre
`26df59f`. Las reglas de los scripts están en [`tools/README.md`](../../tools/README.md).

## Hallazgos

Problemas detectados durante el desarrollo y cómo se resolvieron. En todos los casos se corrigió primero
la spec (o se registró una decisión) y después, si hacía falta, el código.

### Huecos y contradicciones en la spec

| Hallazgo | Resolución | Commit(s) |
| :--- | :--- | :--- |
| **Membresía `VENCIDA` sin flujo:** el modelo incluía `Socio.cambiarMembresia`, pero ningún requisito, historia ni ciclo de vida definía cómo ni cuándo vence una membresía, así que `MEMBRESIA_VENCIDA` no se podía provocar por la API. | Se retira `cambiarMembresia`, el cambio de membresía queda fuera de V1, `VENCIDA` solo se asigna con `Socio.restore` o en la BD, y `US03_CA02` se prueba así. `membresia` pasa a ser `final`. | `d721ff0`, `0e22dd4`, `f26c6f0`, `3d6b3b1` |
| **Nombre del socio sin criterio:** INV-05 (sin espacios en los extremos) no tenía CA ni `pattern` en el contrato. | Se añade US01 CA-06, el `pattern` y el enlace INV-05 ↔ CA-06. | `8aac3f2`, `a532fa7`, `1b23090`, `a78cb3c`, `29de4a9` |
| **Nombre e instructor de la clase:** RF-02 no decía qué hacer con espacios en los extremos. | Se rechazan, como en el socio: INV-16, INV-17, US02 CA-05 y CA-06, y fila `VALIDATION_ERROR` de ADR-0004. | `096bfc3`, `9a96f3f` |
| **Interfaz `GimnasioApi` inexistente:** `CLAUDE.md` y `tech-stack.md` nombraban una sola interfaz, pero el generador crea una por tag. | Se nombran `SociosApi`, `ClasesApi` y `ReservasApi`. | `924bd95`, `213de44` |
| **Error 500 sin contenido definido:** ADR-0004 fijaba `INTERNAL_ERROR`, pero no qué podía exponer la respuesta. El primer borrador de ADR-0007 citaba un enum `ErrorCode` que el contrato no tiene. | ADR-0007: respuesta genérica sin detalles internos, registrada en el índice de ADR. | `8ce83b9` |
| **`Reserva.cancelar(clock)` no podía comprobar INV-14:** la reserva no conoce la hora de la clase. | Nueva firma `cancelar(fechaInicioClase, clock)`. | `dd6765b` |
| **`Location` ausente en `crearClase`:** el contrato solo la declaraba en `registrarSocio`. | Se declara en el 201 de `crearClase`. | `09115a3` |
| **Longitud del email sin especificar.** | Decisión de modelado: longitud por defecto de JPA (255). | `fe3599d` |
| **`pattern` ilegible en Swagger UI.** | `description` en lenguaje natural para nombre e instructor. | `6da51ba` |

### Fallos de configuración

| Hallazgo | Resolución | Commit |
| :--- | :--- | :--- |
| Swagger UI no cargaba el contrato: `static/` no contenía el YAML. | El `pom.xml` copia el contrato a `static/` al compilar. | `f4ad7fd` |
| `/h2-console` respondía 500: en Spring Boot 4 la consola está en un módulo aparte. | Se añade `spring-boot-h2console`. | `458619b` |

### Diferencias D-01 a D-07 (specs ↔ implementación)

Detectadas al generar [`docs/sync-report.md`](../sync-report.md). En todas el código era correcto y la
spec había quedado atrás o describía algo sin historia.

| ID | Diferencia | Commit |
| :--- | :--- | :--- |
| D-01 | El diagrama de capas mencionaba PostgreSQL; ADR-0005 solo acepta H2. | `f7bc956` |
| D-02 | `architecture.md` y ADR-0004 decían "INV-01 a INV-15". | `dc8fff7` |
| D-03 | ADR-0001 preveía una prueba ArchUnit inexistente. | `7feedaf` |
| D-04 | `Clase.cancelar` y `Clase.completar` en el diagrama y en ADR-0002, sin historia. | `4f1e97d` |
| D-05 | RF-01 hablaba de un estado `ACTIVO` del socio que no existe. | `bb0d7e5`, `1a0bf35` |
| D-06 | `consultarReservas` y `consultarDisponibilidad` no declaraban el `400`. | `8c1660d` |
| D-07 | `traceability.md` con un nombre de prueba erróneo y pruebas de persistencia sin listar. | `0ee0c77` |

La evolución completa de las specs, con motivo y compatibilidad del contrato, está en
[`specs/CHANGELOG.md`](../../specs/CHANGELOG.md).

## Pendientes

Tareas abiertas en [`PENDIENTES.md`](../../PENDIENTES.md), todas fuera del alcance de V1:

| Pendiente | Por qué sigue abierto |
| :--- | :--- |
| Comprobar que la clase está `PROGRAMADA` antes de reservar. | En V1 una clase no cambia de estado; basta con INV-10. Hay que especificarlo si se añade `Clase.cancelar`. |
| Condición de carrera en INV-11 e INV-12. | Sin bloqueo; aceptable con H2 en un proyecto de aprendizaje. |
| Consultar el detalle de una clase (RF-05). | Sin operación en el contrato ni historia. |
| Una ruta inexistente responde 500 en lugar de 404. | Requiere ampliar ADR-0004 y ADR-0007 con un código nuevo. |
| Prueba de arquitectura con ArchUnit (ADR-0001). | Retirada del ADR en D-03; la regla se cumple pero no se verifica automáticamente. |
| `Clase.cancelar` y `Clase.completar`. | Retirados del modelo en D-04 hasta que una historia los requiera. |

## Conclusión

La V1 está **completa y coherente con sus specs**:

- Las 6 operaciones del contrato están implementadas y los 24 criterios de aceptación tienen prueba.
- Las 17 invariantes y las 13 filas de ADR-0004 (más ADR-0007) están implementadas y probadas.
- `mvn test`, `spec_lint.py`, `spec_sync.py` y la colección de Postman pasan sin fallos.
- Las 7 diferencias detectadas entre specs y código están resueltas, y cada hueco de spec se cerró en
  la spec antes de tocar el código.

Lo que queda abierto está registrado en `PENDIENTES.md`, justificado y fuera del alcance de V1. Ninguno
de esos puntos contradice una spec vigente.
