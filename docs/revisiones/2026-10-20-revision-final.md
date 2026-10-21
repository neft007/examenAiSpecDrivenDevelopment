# Revisión final con criterio · 2026-10-20

> **Fecha:** 2026-10-20. **Commit revisado:** `26df59f` (código sin cambios desde `065cb8e`).
> **Actualizada:** 2026-10-20 hasta `f5961a2`, con los cambios posteriores del mismo día
> (ver [Actualización posterior](#actualización-posterior-a-la-revisión)).
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

## Actualización posterior a la revisión

Después de `26df59f` se hicieron 55 commits el mismo día, hasta `f5961a2`. Todos cambian documentación o
specs en prosa, salvo `f5961a2`, que añade un script nuevo a `tools/` (ver
[Herramientas añadidas](#herramientas-añadidas)). El código, el `pom.xml`, el contrato, la colección de
Postman, `spec_lint.py` y `spec_sync.py` no cambiaron. Por eso las verificaciones de arriba siguen siendo
válidas; `spec_lint.py` y `spec_sync.py` se volvieron a ejecutar en `f5961a2` con resultado OK (0 errores,
0 advertencias), y `mvn test` pasó antes de `f5961a2` (162 pruebas, 0 fallos).

### Evidencia de la entrega regenerada

La evidencia (`surefire-resumen.md`, `spec_lint.txt`, `spec_sync.txt`) y la
[matriz de trazabilidad](../entrega/matriz-trazabilidad.md) se regeneraron sobre `26df59f` (`c1b5bbe`),
y [`docs/sync-report.md`](../sync-report.md) y [`docs/entrega/README.md`](../entrega/README.md) se
alinearon con ese commit (`cb31a89`, `0d03aac`, `1317499`). La matriz no cambió de contenido: sus 88
pruebas `USxx_CAyy_` existen y no falta ninguna.

### Configuración del generador OpenAPI

| Hallazgo | Resolución | Commit(s) |
| :--- | :--- | :--- |
| La configuración de `openapi-generator-maven-plugin` solo estaba en el `pom.xml`; `tech-stack.md` resumía cuatro parámetros. | Nuevo [`specs/resources/plugins.md`](../../specs/resources/plugins.md): plugin, qué genera, todos los parámetros (comprobados contra el código generado), cómo regenerar y que el código generado no se edita a mano. | `f2412b2` |
| `plugins.md` podía divergir del `pom.xml` sin que nadie lo advirtiera. | Nota de mantenimiento en el propio documento. | `3ae2e46` |
| El prompt de refactorización no decía que un cambio de contrato cambia las interfaces y DTO generados. | Paso de regeneración en el análisis de impacto del prompt 02. | `a209f6c` |

`plugins.md` se enlazó desde la documentación derivada (README, `docs/tecnica/` con las cinco fichas de
operación, `docs/entrega/`, `sync-report.md`, `tools/README.md`, `prompts/`, `CLAUDE.md`) y desde las specs
técnicas: `traceability.md` (`2bb79ee`), `tech-stack.md` (`bf8fc46`), ADR-0006 (`6a3c22c`), índice de ADR
(`bc16f99`), `architecture.md` §3 (`0656baf`), `conventions.md` (`64d8887`), `domain-model.md` §4
(`9c24690`) y `catalogs.md` (`edfe0d1`). Ninguno de estos cambios añade reglas: describen comportamiento
existente.

**Decisiones de criterio:** no se enlazó desde `vision.md`, `requirements/functional.md`, las historias de
usuario ni `domain/lifecycle.md`, porque describen el producto o reglas de negocio sin detalles técnicos (la
relación con los enums ya la cubre `catalogs.md`). Tampoco desde el contrato, que leen los clientes de la API
en Swagger UI, ni con un comentario en el `pom.xml`, que obligaría a tocar el build y a desplazar las líneas
citadas en `plugins.md`.

### Registro de cambios

Cada cambio de `specs/` tiene su entrada en [`specs/CHANGELOG.md`](../../specs/CHANGELOG.md), que pasa de 13
a 23 entradas; los 38 commits que modifican `specs/` aparecen en alguna. Ninguno afecta al contrato.

### Enlaces a esta revisión y comprobación de enlaces

La primera actualización de esta revisión (`a758ea9`, hasta `86828a6`) se enlazó desde los documentos que
muestran resultados fechados en `26df59f`, para explicar por qué siguen vigentes:
[`docs/sync-report.md`](../sync-report.md) (`7c44df7`), [`docs/entrega/README.md`](../entrega/README.md)
(`a552371`), la [matriz de trazabilidad](../entrega/matriz-trazabilidad.md) (`466c915`) y
[`docs/tecnica/pruebas.md`](../tecnica/pruebas.md) (`53f6d21`); y desde los puntos de entrada
[`README.md`](../../README.md) (`ea0f4d3`) y [`docs/tecnica/README.md`](../tecnica/README.md) (`1281a52`).
Desde `d9300c6`, esos enlaces no citan hasta qué commit está actualizada la revisión: solo lo indica la
cabecera de este documento, para no tener que corregir varios archivos cada vez que se actualice.

**Decisiones de criterio:** no se enlazó desde `prompts/README.md`, `CLAUDE.md` ni `docs/tecnica/arquitectura.md`
(ni el resto de `docs/tecnica/`), porque no muestran resultados ni estado: el índice de prompts sirve para
elegir un prompt, y `CLAUDE.md` se carga en cada sesión de la IA y solo recoge fuentes de verdad y reglas.

Una primera comprobación con un script provisional, en `53f6d21`, dio 0 enlaces rotos, 0 anclas rotas y
ningún archivo sin enlaces entrantes. Ese script se convirtió después en `tools/check_links.py`.

### Herramientas añadidas

| Herramienta | Qué comprueba | Resultado | Commit |
| :--- | :--- | :--- | :--- |
| [`tools/check_links.py`](../../tools/check_links.py) | Que los enlaces relativos de los Markdown versionados existan, que las anclas coincidan con un título y que ningún archivo versionado quede sin enlazar (salvo puntos de entrada, infraestructura, `src/` y `.mvn/`). Sin dependencias externas; devuelve `0` si está OK y `1` si hay errores. | ✅ Sobre `d9300c6`: 283 enlaces comprobados en 49 archivos Markdown, 0 errores, 0 huérfanos. | `f5961a2` |

Antes de versionarlo se probó con fallos inyectados en un worktree temporal: detectó un enlace roto, un ancla
rota y un archivo huérfano, e ignoró un enlace falso dentro de un fragmento de código.

Está documentado en [`tools/README.md`](../../tools/README.md) y lo mencionan [`README.md`](../../README.md),
[`docs/tecnica/README.md`](../tecnica/README.md), [`docs/tecnica/pruebas.md`](../tecnica/pruebas.md),
[`docs/entrega/README.md`](../entrega/README.md) y [`docs/sync-report.md`](../sync-report.md), que registra
la ejecución y lo incluye en "Cómo repetir la revisión". La tabla "Verificaciones realizadas" de la entrega
no lo incluye, porque todas sus verificaciones y su evidencia son de `26df59f`.

## Conclusión

La V1 está **completa y coherente con sus specs**:

- Las 6 operaciones del contrato están implementadas y los 24 criterios de aceptación tienen prueba.
- Las 17 invariantes y las 13 filas de ADR-0004 (más ADR-0007) están implementadas y probadas.
- `mvn test`, `spec_lint.py`, `spec_sync.py` y la colección de Postman pasan sin fallos.
- Las 7 diferencias detectadas entre specs y código están resueltas, y cada hueco de spec se cerró en
  la spec antes de tocar el código.

Lo que queda abierto está registrado en `PENDIENTES.md`, justificado y fuera del alcance de V1. Ninguno
de esos puntos contradice una spec vigente. Los cambios posteriores a la revisión solo mejoran la
documentación y la trazabilidad hasta el código generado; no cambian esta conclusión.

## Referencias

La configuración del generador OpenAPI está documentada en
[`specs/resources/plugins.md`](../../specs/resources/plugins.md) (añadido después de esta revisión, en `f2412b2`;
ver [Actualización posterior](#actualización-posterior-a-la-revisión)).

Los diagramas de secuencia de las seis operaciones y el de componentes están en
[`docs/tecnica/diagramas.md`](../tecnica/diagramas.md) (añadido después de esta revisión).
