# Informe de sincronización specs ↔ implementación

> **Fecha:** 2026-10-20 · **Commit revisado:** `6da51ba`; diferencias resueltas hasta `bb0d7e5` ·
> **Pruebas:** 162 ejecutadas, 0 fallos.
> **Fuentes comparadas:** todo [`specs/`](../specs/), `src/main/java`, `src/test/java`, `src/main/resources`,
> `pom.xml` y [`PENDIENTES.md`](../PENDIENTES.md).
>
> Este informe **no corrige** nada: registra diferencias. Cada una se resuelve primero en la spec
> (o en el código, si la spec manda) con su propio commit.

## Resumen

| Área | Estado |
| :--- | :--- |
| Operaciones del contrato (6) | ✅ Las 6 están implementadas; cada controlador implementa su interfaz generada. |
| Criterios de aceptación (24) | ✅ Todos tienen al menos una prueba `USxx_CAyy_*`. |
| Invariantes (INV-01 a INV-17) | ✅ Todas implementadas donde indica `domain-model.md` (INV-15 por ausencia de borrado). |
| Errores ADR-0004 (13 filas) y ADR-0007 | ✅ Todos mapeados en `GlobalExceptionHandler` y probados. |
| Enums vs `catalogs.md` | ✅ Mismos valores y mismo orden en dominio y contrato. |
| Regla de dependencia (ADR-0001) | ✅ Se cumple en el código. La prueba automática (ArchUnit) queda pendiente (D-03). |
| Coherencia interna de las specs | ✅ Las 7 diferencias detectadas están resueltas (ver tabla). |
| Colección de Postman | ✅ Flujo completo y errores verificados contra la aplicación en ejecución (ver [Verificaciones](#verificaciones-realizadas)). |
| Prompts de IA | ✅ 4 prompts reutilizables en `prompts/`; todas las rutas de `specs/` y `docs/` que citan existen (ver [Verificaciones](#verificaciones-realizadas)). |

## Diferencias detectadas

Tipos: **Spec desactualizada** (el código es correcto y la spec quedó atrás), **Hueco de spec**
(la spec no define algo) y **Pendiente** (diferencia conocida y aceptada en V1).

| ID | Tipo | Dónde | Diferencia | Estado | Resolución |
| :--- | :--- | :--- | :--- | :---: | :--- |
| D-01 | Spec desactualizada | [`architecture.md` §2](../specs/architecture/architecture.md) (diagrama) | El nodo de base de datos decía `H2 / PostgreSQL`; ADR-0005 solo acepta H2. | ✅ | `f7bc956`: el nodo dice `H2`. |
| D-02 | Spec desactualizada | [`architecture.md` §2](../specs/architecture/architecture.md) (tabla), [ADR-0004](../specs/adr/0004-codigos-de-error.md) (cabecera) | Decían "INV-01 a INV-15" aunque existen INV-16 e INV-17. | ✅ | `dc8fff7`: "INV-01 a INV-17". El Contexto de ADR-0001 conserva "INV-01 a INV-15" porque describe la situación al escribirse. |
| D-03 | Pendiente | [ADR-0001](../specs/adr/0001-arquitectura-hexagonal.md) (Consecuencias) | El ADR preveía una prueba ArchUnit que no existe; ArchUnit no está en el `pom.xml`. | ✅ | `7feedaf`: se retira la verificación del ADR y la prueba queda en `PENDIENTES.md`, fuera de V1. |
| D-04 | Hueco de spec | [`domain-model.md` §2](../specs/architecture/domain-model.md), [ADR-0002](../specs/adr/0002-transiciones-como-acciones.md) | El diagrama incluía `Clase.cancelar(clock)` y `Clase.completar(clock)`, y ADR-0002 mencionaba `Clase.cancelar()`, sin historia ni implementación. | ✅ | `4f1e97d`: se quitan del diagrama y de ADR-0002; quedan en `PENDIENTES.md`. ADR-0002 conserva `POST /api/v1/clases/{id}/cancelar` como ruta futura. |
| D-05 | Hueco de spec | [RF-01](../specs/requirements/functional.md) | Decía "El socio se crea en estado `ACTIVO`", un estado que no existe en ningún catálogo. | ✅ | `bb0d7e5` y el commit `spec:` siguiente: se elimina la frase; RF-01 solo indica que la membresía se asigna por defecto `ACTIVA`. |
| D-06 | Spec desactualizada | [`gimnasio-openapi.yaml`](../specs/api/gimnasio-openapi.yaml) (`consultarReservas`, `consultarDisponibilidad`) | No declaraban `400`, aunque la API responde `400 VALIDATION_ERROR` ante un UUID mal formado o un `estado` fuera de catálogo. | ✅ | `8c1660d`: ambas declaran `'400': ValidationError`. |
| D-07 | Spec desactualizada | [`traceability.md`](../specs/traceability.md) | Nombre incorrecto de la prueba de arranque y pruebas de persistencia sin listar. | ✅ | `0ee0c77`: `GimnasioReservasApplicationTests`, `ClasePersistenceAdapterTest` (US02_CA01), US01_CA01, US03_CA01 y `findBySocioId`. |

## Pendientes conocidos (ya registrados)

Recogidos en [`PENDIENTES.md`](../PENDIENTES.md); fuera del alcance de V1.

| Pendiente | Spec afectada | Situación |
| :--- | :--- | :--- |
| No se comprueba que la clase esté `PROGRAMADA` antes de reservar. | RF-03 | En V1 una clase no puede cambiar de estado; basta con INV-10. |
| Carrera en INV-11 e INV-12 con reservas simultáneas. | RF-03 | Sin bloqueo; aceptable con H2 en un proyecto de aprendizaje. |
| "Consultar el detalle de una clase" no tiene operación. | RF-05 | Sin historia ni contrato. |
| Una ruta inexistente responde 500 en lugar de 404. | ADR-0004, ADR-0007 | Requiere ampliar los ADR con un código nuevo. |
| Prueba de arquitectura con ArchUnit. | ADR-0001 | Procede de D-03. |
| `Clase.cancelar` y `Clase.completar`. | `lifecycle.md` | Procede de D-04; las transiciones de la clase siguen en `lifecycle.md`, pero sin historia ni operación. |

## Verificaciones realizadas

- **Contrato ↔ controladores:** `SocioController implements SociosApi`, `ClaseController implements ClasesApi`,
  `ReservaController implements ReservasApi`; las 6 operaciones devuelven los HTTP de éxito del contrato
  (201 con `Location` en `registrarSocio` y `crearClase`; 201 sin `Location` en `reservarPlaza`; 200 en el resto).
- **Puertos:** las operaciones de `architecture.md` §4 coinciden con las interfaces de `application.port.in/out`.
- **Invariantes:** cada una se encuentra en la clase que indica `domain-model.md` §3, marcada con un comentario `INV-xx`.
- **Errores:** los 13 `code` de ADR-0004 aparecen en `GlobalExceptionHandler`; `GlobalExceptionHandlerTest`
  cubre todas las filas y el 500 sin detalles internos.
- **Stack:** Java 25, Spring Boot 4.1.1, openapi-generator 7.22.0 y springdoc 3.1.1 en `pom.xml`, coherentes con
  `tech-stack.md`; la consola H2 y Swagger UI responden en `/h2-console` y `/swagger-ui.html`.
- **Colección de Postman:** [`postman/gimnasio-reservas-flujo.postman_collection.json`](../postman/gimnasio-reservas-flujo.postman_collection.json)
  recorre el flujo completo de US01 a US05 y los casos de error de ADR-0004. Se ejecutó con newman 6.2.1
  contra la aplicación local dos veces seguidas, sin reiniciarla: 35 peticiones, 65 aserciones, 0 fallos
  en cada ejecución. `MEMBRESIA_VENCIDA` y `CLASE_YA_COMENZADA` no se pueden provocar por la API en V1,
  así que solo se cubren en las pruebas automáticas. Detalle en
  [`docs/tecnica/pruebas.md`](tecnica/pruebas.md#colección-de-postman).
- **Prompts de IA:** [`prompts/`](../prompts/) contiene 4 prompts reutilizables usados con Claude Code
  (dominio, operación, refactorización y documentación). Cada uno indica cuándo usarlo y las variables
  que acepta, y en conjunto documentan el flujo SDD seguido durante el desarrollo. Las 13 rutas de
  `specs/` y `docs/` que mencionan existen en el repositorio.
- **Documentación derivada:** [`docs/tecnica/`](tecnica/README.md) se generó a partir de este mismo estado.

## Cómo repetir la revisión

1. `./mvnw clean test` y anotar el número de pruebas.
2. Comparar `paths` del contrato con los controladores, y `traceability.md` con `src/test/java`.
3. Buscar en `src/main/java` los comentarios `INV-xx` y contrastarlos con `domain-model.md` §3.
4. Actualizar este informe y `docs/tecnica/` en un commit `docs:`.
