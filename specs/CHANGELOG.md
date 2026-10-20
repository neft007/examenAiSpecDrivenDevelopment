# Registro de cambios de las specs

Cambios en `specs/` desde su creación, del más reciente al más antiguo. Cada entrada agrupa uno o varios
commits que responden al mismo motivo. Las fechas y los commits salen de `git log -- specs/`.

**Formato de cada entrada:** fecha, descripción, motivo, archivos de spec modificados, compatibilidad
del contrato y commits previstos. En las entradas ya implementadas, "Commits previstos" lista los commits
reales: primero los que cambian la spec y después los que la implementan.

**Versión del contrato.** `specs/api/gimnasio-openapi.yaml` declara `version: 1.0.0` desde su creación
(`ad66f17`) y no ha cambiado, porque todos los cambios se hicieron antes de la primera entrega. La
compatibilidad indica qué incremento habría correspondido según SemVer si el contrato ya estuviera publicado:

| Incremento | Cuándo |
| :--- | :--- |
| **Mayor** | Un cliente existente puede dejar de funcionar (por ejemplo, una validación más restrictiva). |
| **Menor** | Se añade algo compatible (por ejemplo, una cabecera nueva en una respuesta). |
| **Parche** | Solo cambia documentación o se declara un comportamiento que la API ya tenía. |
| **No afecta** | El contrato no cambia. |

---

## 2026-10-20 · Corrección de RF-01: eliminar la línea redundante sobre la membresía

- **Descripción:** se elimina de RF-01 la línea "El socio se crea con membresía `ACTIVA`". RF-01 conserva
  "Estado de membresía: `ACTIVA` o `VENCIDA`. Se asigna por defecto `ACTIVA`".
- **Motivo:** la línea, añadida al resolver D-05, repetía lo que ya decía el dato "Estado de membresía".
- **Archivos de spec modificados:** `requirements/functional.md`.
- **Compatibilidad del contrato:** no afecta.
- **Commits previstos:** `1a0bf35` (spec).

## 2026-10-20 · Corrección de las diferencias D-01 a D-07 entre specs y código

- **Descripción:** se corrigen las siete diferencias detectadas en `docs/sync-report.md`:

  | ID | Cambio |
  | :--- | :--- |
  | D-01 | El diagrama de capas muestra solo `H2`, sin PostgreSQL. |
  | D-02 | `architecture.md` y la cabecera de ADR-0004 dicen "INV-01 a INV-17". |
  | D-03 | ADR-0001 deja de prever una prueba ArchUnit, que queda fuera de V1. |
  | D-04 | `Clase.cancelar` y `Clase.completar` salen del diagrama de clases y de ADR-0002. |
  | D-05 | RF-01 deja de mencionar un estado `ACTIVO` del socio que no existe en ningún catálogo. |
  | D-06 | El contrato declara la respuesta `400` en `consultarReservas` y `consultarDisponibilidad`. |
  | D-07 | `traceability.md` corrige el nombre de la prueba de arranque y lista las pruebas de persistencia que faltaban. |

- **Motivo:** las specs habían quedado atrás respecto al código (D-01, D-02, D-06, D-07) o describían
  elementos sin historia ni implementación (D-03, D-04, D-05). En todos los casos el código era correcto.
- **Archivos de spec modificados:** `architecture/architecture.md`, `architecture/domain-model.md`,
  `adr/0001-arquitectura-hexagonal.md`, `adr/0002-transiciones-como-acciones.md`,
  `adr/0004-codigos-de-error.md`, `api/gimnasio-openapi.yaml`, `requirements/functional.md`,
  `traceability.md`.
- **Compatibilidad del contrato:** **parche** (D-06): se declara un `400 VALIDATION_ERROR` que la API ya
  devolvía. El resto no afecta al contrato.
- **Commits previstos:** `f7bc956` (D-01), `dc8fff7` (D-02), `8c1660d` (D-06), `0ee0c77` (D-07),
  `7feedaf` (D-03), `4f1e97d` (D-04), `bb0d7e5` (D-05). No requieren cambios de código.

## 2026-10-20 · Descripción en lenguaje natural de nombre e instructor

- **Descripción:** `RegistrarSocioRequest.nombre`, `CrearClaseRequest.nombre` y
  `CrearClaseRequest.instructor` incluyen `description: 3 a 100 caracteres, sin espacios al inicio ni al final.`
- **Motivo:** Swagger UI mostraba solo el `pattern` `^\S(.*\S)?$`, poco legible.
- **Archivos de spec modificados:** `api/gimnasio-openapi.yaml`.
- **Compatibilidad del contrato:** **parche**: solo documentación; la validación no cambia.
- **Commits previstos:** `6da51ba` (contract).

## 2026-10-20 · Nueva firma de `Reserva.cancelar(fechaInicioClase, clock)`

- **Descripción:** el diagrama de clases cambia `Reserva.cancelar(clock)` por
  `Reserva.cancelar(fechaInicioClase, clock)`.
- **Motivo:** INV-14 (al menos 2 horas de antelación) necesita la fecha de inicio de la clase, que la
  reserva no conoce; así la regla vive en el dominio y el servicio solo aporta la fecha.
- **Archivos de spec modificados:** `architecture/domain-model.md`.
- **Compatibilidad del contrato:** no afecta.
- **Commits previstos:** `dd6765b` (design); `85c42b7` (dominio), `9d333f2` (aplicación).

## 2026-10-20 · Cabecera `Location` en la respuesta 201 de `crearClase`

- **Descripción:** el contrato declara la cabecera `Location` en el `201` de `crearClase`, como ya hacía
  `registrarSocio`.
- **Motivo:** el controlador de clases debía devolver la URL del recurso creado y el contrato no lo
  declaraba.
- **Archivos de spec modificados:** `api/gimnasio-openapi.yaml`.
- **Compatibilidad del contrato:** **menor**: se añade una cabecera de respuesta.
- **Commits previstos:** `09115a3` (contract); `fb7c2ed` (web).

## 2026-10-20 · Ajuste de ADR-0004: INV-16 e INV-17 en la fila `VALIDATION_ERROR`

- **Descripción:** la fila `400 VALIDATION_ERROR` de ADR-0004 incluye INV-16 e INV-17. La cabecera del ADR
  se actualizó más tarde, con D-02.
- **Motivo:** las invariantes nuevas de nombre e instructor de la clase se rechazan con `VALIDATION_ERROR`
  y el ADR debía reflejarlo.
- **Archivos de spec modificados:** `adr/0004-codigos-de-error.md`.
- **Compatibilidad del contrato:** no afecta: el código de error ya existía.
- **Commits previstos:** `9a96f3f` (design). El mapeo ya estaba en `GlobalExceptionHandler` (`ValidationException`).

## 2026-10-20 · Adición de INV-16 e INV-17: nombre e instructor de Clase

- **Descripción:** RF-02 exige que el nombre y el instructor de una clase tengan entre 3 y 100 caracteres
  sin espacios al inicio ni al final. Se añaden US02 CA-05 y CA-06, las invariantes INV-16 e INV-17
  (garantizadas en `Clase.crear()`) y el `pattern` en `CrearClaseRequest`.
- **Motivo:** RF-02 no decía qué hacer con espacios en los extremos. Se eligió rechazarlos en lugar de
  recortarlos, igual que el nombre del socio (INV-05).
- **Archivos de spec modificados:** `requirements/functional.md`, `user-stories/US02-crear-clase.md`,
  `architecture/domain-model.md`, `traceability.md`, `api/gimnasio-openapi.yaml`.
- **Compatibilidad del contrato:** **mayor**: la validación de `CrearClaseRequest` pasa a ser más
  restrictiva.
- **Commits previstos:** `096bfc3` (spec); `6f84be8` (dominio), `eb9a404` (persistencia),
  `d18453f` (aplicación), `fb7c2ed` (web).

## 2026-10-20 · Creación de ADR-0007: error interno genérico

- **Descripción:** nuevo ADR-0007: toda excepción no mapeada por ADR-0004 responde
  `500 { code: INTERNAL_ERROR, message: "Unexpected error", details: [] }` y la excepción se registra en
  el log con nivel `ERROR`.
- **Motivo:** ADR-0004 fijaba el código `INTERNAL_ERROR`, pero no qué podía contener la respuesta. Sin
  decisión, Spring respondía con su formato por defecto y podía exponer detalles internos.
- **Archivos de spec modificados:** `adr/0007-error-interno-generico.md`, `adr/README.md`.
- **Compatibilidad del contrato:** no afecta: la respuesta `InternalError` ya estaba declarada.
- **Commits previstos:** `8ce83b9` (design); `1554661` (web, manejador de `Exception`) y `79a7376`
  (web, `GlobalExceptionHandlerTest` con todas las filas).

## 2026-10-20 · Interfaces generadas por tag

- **Descripción:** `tech-stack.md` y `CLAUDE.md` nombran las interfaces generadas por tag (`SociosApi`,
  `ClasesApi`, `ReservasApi`) en lugar de una única `GimnasioApi`.
- **Motivo:** openapi-generator con `useTags=true` genera una interfaz por tag; `GimnasioApi` no existía.
- **Archivos de spec modificados:** `architecture/tech-stack.md`.
- **Compatibilidad del contrato:** no afecta.
- **Commits previstos:** `924bd95` (design); `213de44` (design, fuentes de verdad de `CLAUDE.md`).

## 2026-10-19 · Longitud del email en persistencia

- **Descripción:** decisión de modelado: el email se almacena con la longitud por defecto de JPA (255).
- **Motivo:** ninguna spec define una longitud máxima del email, y la persistencia necesitaba un valor.
- **Archivos de spec modificados:** `architecture/domain-model.md`.
- **Compatibilidad del contrato:** no afecta.
- **Commits previstos:** `fe3599d` (design); `30c4cf4` (persistencia).

## 2026-10-19 · Nombre del socio sin espacios en los extremos (US01 CA-06)

- **Descripción:** RF-01 e INV-05 exigen que el nombre del socio no tenga espacios al inicio ni al final.
  Se añade US01 CA-06 y el `pattern` en `RegistrarSocioRequest.nombre`, y se enlaza INV-05 con el nuevo criterio.
- **Motivo:** INV-05 no tenía criterio de aceptación y la regla de los espacios no estaba especificada.
- **Archivos de spec modificados:** `requirements/functional.md`, `architecture/domain-model.md`,
  `user-stories/US01-registrar-socio.md`, `traceability.md`, `api/gimnasio-openapi.yaml`.
- **Compatibilidad del contrato:** **mayor**: la validación de `RegistrarSocioRequest` pasa a ser más
  restrictiva.
- **Commits previstos:** `8aac3f2` (RF-01 e INV-05, junto con el dominio de Socio), `a532fa7` (contract),
  `1b23090` (spec), `a78cb3c` (design); `29de4a9` (test).

## 2026-10-19 · Cambio de membresía fuera del alcance de V1

- **Descripción:** se retira `Socio.cambiarMembresia` del modelo, `vision.md` declara el cambio de
  membresía fuera de alcance y `lifecycle.md` aclara que `VENCIDA` solo se asigna con `Socio.restore` o en
  la base de datos.
- **Motivo:** ningún requisito, historia ni regla de ciclo de vida definía cómo ni cuándo cambia la
  membresía.
- **Archivos de spec modificados:** `architecture/domain-model.md`, `vision.md`, `domain/lifecycle.md`,
  `traceability.md`.
- **Compatibilidad del contrato:** no afecta.
- **Commits previstos:** `d721ff0` (design), `0e22dd4` (spec), `f26c6f0` (design); `3d6b3b1` (refactor:
  `membresia` final en `Socio`).

## 2026-10-18 · Specs iniciales y contrato v1.0.0

- **Descripción:** primera versión de las specs: visión, requerimientos, ciclo de vida, catálogos,
  historias US01 a US05, convenciones, modelo de dominio, arquitectura, stack, trazabilidad, ADR-0001 a
  ADR-0006 y el contrato OpenAPI.
- **Motivo:** punto de partida del desarrollo Spec-Driven.
- **Archivos de spec modificados:** todos los de `specs/` salvo ADR-0007 y este registro.
- **Compatibilidad del contrato:** versión inicial `1.0.0`.
- **Commits previstos:** `a456ecd` (spec), `0d4de7b` (convenciones), `7a2c18a` (design), `c794302`
  (design), `ad66f17` (contract).
