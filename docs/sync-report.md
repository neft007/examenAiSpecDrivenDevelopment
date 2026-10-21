# Informe de sincronización specs ↔ implementación

> **Última verificación:** 2026-10-20, commit `26df59f`.
> **Fuentes comparadas:** todo [`specs/`](../specs/), `src/main/java`, `src/test/java`, `src/main/resources`
> y `pom.xml`.
>
> **Alcance.** Este informe solo registra:
>
> 1. Las diferencias entre specs e implementación y cómo se resolvieron.
> 2. Las verificaciones realizadas: `mvn test`, `spec_lint.py`, `spec_sync.py` y la colección de Postman.
>
> No lista los archivos de documentación ni las entradas de [`PENDIENTES.md`](../PENDIENTES.md).
> Tampoco corrige nada: cada diferencia se resuelve primero en la spec (o en el código, si la spec manda)
> con su propio commit.

## Resumen

| Área | Estado |
| :--- | :--- |
| Operaciones del contrato (6) | ✅ Las 6 están implementadas; cada controlador implementa su interfaz generada. |
| Criterios de aceptación (24) | ✅ Todos tienen al menos una prueba `USxx_CAyy_*`. |
| Invariantes (INV-01 a INV-17) | ✅ Todas implementadas donde indica `domain-model.md` (INV-15 por ausencia de borrado). |
| Errores ADR-0004 (13 filas) y ADR-0007 | ✅ Todos mapeados en `GlobalExceptionHandler` y probados. |
| Enums vs `catalogs.md` | ✅ Mismos valores y mismo orden en dominio y contrato. |
| Regla de dependencia (ADR-0001) | ✅ Se cumple en el código; sin prueba automática (D-03). |
| Diferencias detectadas | ✅ Las 7 están resueltas. Quedan 4 diferencias aceptadas en V1. |

## Diferencias detectadas

Tipos: **Spec desactualizada** (el código es correcto y la spec quedó atrás), **Hueco de spec**
(la spec no define algo) y **Pendiente** (diferencia conocida y aceptada en V1).

| ID | Tipo | Dónde | Diferencia | Estado | Resolución |
| :--- | :--- | :--- | :--- | :---: | :--- |
| D-01 | Spec desactualizada | [`architecture.md` §2](../specs/architecture/architecture.md) (diagrama) | El nodo de base de datos decía `H2 / PostgreSQL`; ADR-0005 solo acepta H2. | ✅ | `f7bc956`: el nodo dice `H2`. |
| D-02 | Spec desactualizada | [`architecture.md` §2](../specs/architecture/architecture.md) (tabla), [ADR-0004](../specs/adr/0004-codigos-de-error.md) (cabecera) | Decían "INV-01 a INV-15" aunque existen INV-16 e INV-17. | ✅ | `dc8fff7`: "INV-01 a INV-17". El Contexto de ADR-0001 conserva "INV-01 a INV-15" porque describe la situación al escribirse. |
| D-03 | Pendiente | [ADR-0001](../specs/adr/0001-arquitectura-hexagonal.md) (Consecuencias) | El ADR preveía una prueba ArchUnit que no existe; ArchUnit no está en el `pom.xml`. | ✅ | `7feedaf`: se retira la verificación del ADR; la prueba queda fuera de V1. |
| D-04 | Hueco de spec | [`domain-model.md` §2](../specs/architecture/domain-model.md), [ADR-0002](../specs/adr/0002-transiciones-como-acciones.md) | El diagrama incluía `Clase.cancelar(clock)` y `Clase.completar(clock)`, y ADR-0002 mencionaba `Clase.cancelar()`, sin historia ni implementación. | ✅ | `4f1e97d`: se quitan del diagrama y de ADR-0002; quedan fuera de V1. ADR-0002 conserva `POST /api/v1/clases/{id}/cancelar` como ruta futura. |
| D-05 | Hueco de spec | [RF-01](../specs/requirements/functional.md) | Decía "El socio se crea en estado `ACTIVO`", un estado que no existe en ningún catálogo. | ✅ | `bb0d7e5` y `1a0bf35`: se elimina la frase; RF-01 solo indica que la membresía se asigna por defecto `ACTIVA`. |
| D-06 | Spec desactualizada | [`gimnasio-openapi.yaml`](../specs/api/gimnasio-openapi.yaml) (`consultarReservas`, `consultarDisponibilidad`) | No declaraban `400`, aunque la API responde `400 VALIDATION_ERROR` ante un UUID mal formado o un `estado` fuera de catálogo. | ✅ | `8c1660d`: ambas declaran `'400': ValidationError`. |
| D-07 | Spec desactualizada | [`traceability.md`](../specs/traceability.md) | Nombre incorrecto de la prueba de arranque y pruebas de persistencia sin listar. | ✅ | `0ee0c77`: `GimnasioReservasApplicationTests`, `ClasePersistenceAdapterTest` (US02_CA01), US01_CA01, US03_CA01 y `findBySocioId`. |

### Diferencias aceptadas en V1

Diferencias que siguen abiertas entre lo que dicen las specs y lo que hace el código, aceptadas para esta versión:

| Diferencia | Spec afectada | Por qué se acepta |
| :--- | :--- | :--- |
| No se comprueba que la clase esté `PROGRAMADA` antes de reservar. | RF-03 | En V1 una clase no puede cambiar de estado; basta con INV-10. |
| Dos reservas simultáneas pueden saltarse INV-11 o INV-12. | RF-03 | Sin bloqueo; aceptable con H2 en un proyecto de aprendizaje. |
| "Consultar el detalle de una clase" no tiene operación. | RF-05 | Sin historia ni contrato. |
| Una ruta inexistente responde 500 en lugar de 404. | ADR-0004, ADR-0007 | Requiere ampliar los ADR con un código nuevo. |

## Verificaciones realizadas

### Ejecuciones

| Verificación | Comando | Resultado | Commit |
| :--- | :--- | :--- | :--- |
| Pruebas automáticas | `./mvnw clean test` | ✅ 162 pruebas, 0 fallos, 0 errores. | `26df59f` |
| Coherencia de specs | `python tools/spec_lint.py` | ✅ OK: 6 operaciones, 0 errores, 0 advertencias. | `26df59f` |
| Specs ↔ código y pruebas | `python tools/spec_sync.py` | ✅ OK: 6 operaciones en 3 controladores, 24 criterios, 110 métodos de prueba, 21 clases de `traceability.md`, 0 errores. Hay 22 pruebas sin prefijo `USxx_CAyy_`, que se admiten. | `26df59f` |
| Aplicación en ejecución | `npx newman run postman/gimnasio-reservas-flujo.postman_collection.json` | ✅ 35 peticiones, 65 aserciones, 0 fallos, contra la aplicación recién arrancada (`./mvnw spring-boot:run`). `MEMBRESIA_VENCIDA` y `CLASE_YA_COMENZADA` no se pueden provocar por la API en V1. | `26df59f` |

La evidencia de estas ejecuciones ([`surefire-resumen.md`](entrega/evidencia/surefire-resumen.md),
[`spec_lint.txt`](entrega/evidencia/spec_lint.txt) y [`spec_sync.txt`](entrega/evidencia/spec_sync.txt))
se generó sobre el mismo commit, `26df59f`.

### Revisión manual

- **Contrato ↔ controladores:** `SocioController implements SociosApi`, `ClaseController implements ClasesApi`,
  `ReservaController implements ReservasApi`; las 6 operaciones devuelven los HTTP de éxito del contrato
  (201 con `Location` en `registrarSocio` y `crearClase`; 201 sin `Location` en `reservarPlaza`; 200 en el resto).
- **Puertos:** las operaciones de `architecture.md` §4 coinciden con las interfaces de `application.port.in/out`.
- **Invariantes:** cada una se encuentra en la clase que indica `domain-model.md` §3, marcada con un comentario `INV-xx`.
- **Errores:** los 13 `code` de ADR-0004 aparecen en `GlobalExceptionHandler`; `GlobalExceptionHandlerTest`
  cubre todas las filas y el 500 sin detalles internos.
- **Stack:** Java 25, Spring Boot 4.1.1, openapi-generator 7.22.0 y springdoc 3.1.1 en `pom.xml`, coherentes con
  `tech-stack.md`; la consola H2 y Swagger UI responden en `/h2-console` y `/swagger-ui.html`.
- **Generador OpenAPI:** [`specs/resources/plugins.md`](../specs/resources/plugins.md) documenta la configuración
  del generador. Coincide con el `pom.xml` (líneas 97–132) y con el código que produce
  `./mvnw clean generate-sources`: `SociosApi`, `ClasesApi`, `ReservasApi`, 9 DTO y 3 enums.
- **Registro de cambios de las specs:** [`specs/CHANGELOG.md`](../specs/CHANGELOG.md) está actualizado
  hasta `2bb79ee`. Tiene 15 entradas, todas con fecha, descripción, motivo, archivos modificados, compatibilidad
  del contrato y commits. Los 30 commits que modifican `specs/` (`git log -- specs/`, sin contar los del
  propio `CHANGELOG.md`) aparecen en alguna entrada.
- **Revisión final:** la revisión final con criterio
  ([`docs/revisiones/2026-10-20-revision-final.md`](revisiones/2026-10-20-revision-final.md)) recoge las
  verificaciones, los hallazgos con el commit que los resolvió, los pendientes y el estado final de la V1.

## Cómo repetir la revisión

1. Ejecutar `./mvnw clean test`, `python tools/spec_lint.py` y `python tools/spec_sync.py`. Si cambió el
   código o la colección, ejecutar también la colección de Postman con la aplicación levantada.
2. Revisar a mano lo que los scripts no cubren: los puertos, las invariantes y el stack.
3. Anotar aquí las diferencias nuevas y actualizar la tabla de ejecuciones, en un commit `docs:`.
