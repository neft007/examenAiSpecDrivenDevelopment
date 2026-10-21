# Matriz de trazabilidad de la entrega

> **Fuentes:** [`specs/traceability.md`](../../specs/traceability.md),
> [`specs/requirements/functional.md`](../../specs/requirements/functional.md),
> [`specs/user-stories/`](../../specs/user-stories/),
> [`specs/architecture/domain-model.md`](../../specs/architecture/domain-model.md) §3,
> [ADR-0004](../../specs/adr/0004-codigos-de-error.md) y los métodos de prueba de `src/test/java`.
>
> **Generado sobre `26df59f`** (2026-10-20).

Cada fila cruza un criterio de aceptación con la invariante que lo respalda, el error que produce y las
pruebas que lo verifican, agrupadas por capa. Las pruebas se citan como `Clase.método`.

## Resumen

| RF | Historia | Operación (`operationId`) | Caso de uso / servicio | Invariantes | CA | Pruebas `USxx_CAyy_` |
| :--- | :--- | :--- | :--- | :--- | :---: | :---: |
| RF-01 | [US01](../../specs/user-stories/US01-registrar-socio.md) | `registrarSocio` | `RegistrarSocioUseCase` / `RegistrarSocioService` | INV-01 a INV-05 | 6 | 18 |
| RF-02 | [US02](../../specs/user-stories/US02-crear-clase.md) | `crearClase` | `CrearClaseUseCase` / `CrearClaseService` | INV-06, 07, 08, 16, 17 | 6 | 26 |
| RF-03 | [US03](../../specs/user-stories/US03-reservar-plaza.md) | `reservarPlaza` | `ReservarPlazaUseCase` / `ReservarPlazaService` | INV-09 a INV-12 | 5 | 18 |
| RF-04 | [US04](../../specs/user-stories/US04-cancelar-reserva.md) | `cancelarReserva` | `CancelarReservaUseCase` / `CancelarReservaService` | INV-13, 14, 15 | 3 | 13 |
| RF-05 | [US05](../../specs/user-stories/US05-consultar-reservas.md) | `consultarReservas`, `consultarDisponibilidad` | `ConsultarReservasUseCase`, `ConsultarDisponibilidadUseCase` | — | 4 | 13 |
| | | | | **17** | **24** | **88** |

Cada criterio tiene al menos dos pruebas, en capas distintas. La columna cuenta métodos; las pruebas
parametrizadas ejecutan más casos (ver [`evidencia/surefire-resumen.md`](evidencia/surefire-resumen.md)).

## RF-01 · US01 Registrar socio · `registrarSocio`

`POST /api/v1/socios` → `RegistrarSocioService`

| CA | Invariante | Error | Dominio | Aplicación | Persistencia | Web |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| CA-01 Registra con membresía `ACTIVA` | — | — | `US01RegistrarSocioTest.US01_CA01_registersSocioWithActiveMembership` | `RegistrarSocioServiceTest.US01_CA01_registersSocioWithActiveMembership` | `SocioPersistenceAdapterTest.US01_CA01_savesAndRestoresAllFields` | `SocioControllerRegisterTest.US01_CA01_registersSocioWithActiveMembership` |
| CA-02 DNI duplicado | INV-02 | 409 `DNI_DUPLICADO` | — | `RegistrarSocioServiceTest.US01_CA02_rejectsDuplicatedDni` | `SocioPersistenceAdapterTest.US01_CA02_rejectsDuplicatedDniAtDatabaseLevel` | `SocioControllerRegisterTest.US01_CA02_rejectsDuplicatedDni` |
| CA-03 DNI con formato inválido | INV-01 | 400 `VALIDATION_ERROR` | `US01RegistrarSocioTest.US01_CA03_rejectsDniWithInvalidFormat` | — | — | `SocioControllerRegisterTest.US01_CA03_rejectsDniWithInvalidFormat` |
| CA-04 Email duplicado | INV-03 | 409 `EMAIL_DUPLICADO` | — | `RegistrarSocioServiceTest.US01_CA04_rejectsDuplicatedEmail` | `SocioPersistenceAdapterTest.US01_CA04_rejectsDuplicatedEmailAtDatabaseLevel` | `SocioControllerRegisterTest.US01_CA04_rejectsDuplicatedEmail` |
| CA-05 Email con formato inválido | INV-04 | 400 `VALIDATION_ERROR` | `US01RegistrarSocioTest.US01_CA05_rejectsEmailWithInvalidFormat` | — | — | `SocioControllerRegisterTest.US01_CA05_rejectsEmailWithInvalidFormat` |
| CA-06 Nombre inválido | INV-05 | 400 `VALIDATION_ERROR` | `US01RegistrarSocioTest.US01_CA06_rejectsInvalidNombre`, `US01_CA06_rejectsNombreLongerThan100Characters`, `US01_CA06_acceptsNombreAtBoundaries` | — | — | `SocioControllerRegisterTest.US01_CA06_rejectsNombreWithLeadingOrTrailingSpaces` |

## RF-02 · US02 Crear clase · `crearClase`

`POST /api/v1/clases` → `CrearClaseService` (todas las reglas en `Clase.crear()`)

| CA | Invariante | Error | Dominio | Aplicación | Persistencia | Web |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| CA-01 Crea en `PROGRAMADA` | — | — | `US02CrearClaseTest.US02_CA01_createsClaseInProgramadaState`, `US02_CA01_acceptsLimitValues` | `CrearClaseServiceTest.US02_CA01_createsClaseInProgramadaState` | `ClasePersistenceAdapterTest.US02_CA01_savesAndRestoresAllFields` | `ClaseControllerCreateTest.US02_CA01_createsClaseInProgramadaState` |
| CA-02 Fecha en el pasado | INV-06 | 400 `VALIDATION_ERROR` | `US02CrearClaseTest.US02_CA02_rejectsStartDateInThePast`, `US02_CA02_rejectsStartDateEqualToNow`, `US02_CA02_rejectsMissingStartDate` | `CrearClaseServiceTest.US02_CA02_rejectsStartDateInThePast` | — | `ClaseControllerCreateTest.US02_CA02_rejectsStartDateInThePast` |
| CA-03 Aforo fuera de 1–50 | INV-07 | 400 `VALIDATION_ERROR` | `US02CrearClaseTest.US02_CA03_rejectsAforoOutOfRange` | `CrearClaseServiceTest.US02_CA03_rejectsAforoOutOfRange` | — | `ClaseControllerCreateTest.US02_CA03_rejectsAforoOutOfRange` |
| CA-04 Duración fuera de 1–180 | INV-08 | 400 `VALIDATION_ERROR` | `US02CrearClaseTest.US02_CA04_rejectsDuracionOutOfRange` | `CrearClaseServiceTest.US02_CA04_rejectsDuracionOutOfRange` | — | `ClaseControllerCreateTest.US02_CA04_rejectsDuracionOutOfRange` |
| CA-05 Nombre inválido | INV-16 | 400 `VALIDATION_ERROR` | `US02CrearClaseTest.US02_CA05_rejectsInvalidNombre`, `US02_CA05_rejectsNombreLongerThan100Characters`, `US02_CA05_rejectsNombreWithLeadingOrTrailingWhitespace` | `CrearClaseServiceTest.US02_CA05_rejectsNombreWithLeadingOrTrailingWhitespace` | — | `ClaseControllerCreateTest.US02_CA05_rejectsNombreWithLeadingOrTrailingSpaces` |
| CA-06 Instructor inválido | INV-17 | 400 `VALIDATION_ERROR` | `US02CrearClaseTest.US02_CA06_rejectsInvalidInstructor`, `US02_CA06_rejectsInstructorLongerThan100Characters`, `US02_CA06_rejectsInstructorWithLeadingOrTrailingWhitespace` | `CrearClaseServiceTest.US02_CA06_rejectsInstructorWithLeadingOrTrailingWhitespace` | — | `ClaseControllerCreateTest.US02_CA06_rejectsInstructorWithLeadingOrTrailingSpaces` |

## RF-03 · US03 Reservar plaza · `reservarPlaza`

`POST /api/v1/clases/{claseId}/reservas` → `ReservarPlazaService` (INV-09 a INV-12 en el servicio)

| CA | Invariante | Error | Dominio | Aplicación | Persistencia | Web |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| CA-01 Reserva `CONFIRMADA` | — | — | `US03ReservarPlazaTest.US03_CA01_createsReservaInConfirmadaState`, `US03_CA01_generatesUniqueReservaIds` | `ReservarPlazaServiceTest.US03_CA01_createsConfirmadaReserva` | `ReservaPersistenceAdapterTest.US03_CA01_savesAndRestoresAllFields` | `ReservaControllerReservarTest.US03_CA01_createsConfirmadaReserva` |
| CA-02 Membresía vencida | INV-09 | 409 `MEMBRESIA_VENCIDA` | — | `ReservarPlazaServiceTest.US03_CA02_rejectsSocioWithExpiredMembership` | — | `ReservaControllerReservarTest.US03_CA02_rejectsSocioWithExpiredMembership` |
| CA-03 Aforo completo | INV-11 | 409 `AFORO_COMPLETO` | — | `ReservarPlazaServiceTest.US03_CA03_rejectsWhenClaseIsFull`, `US03_CA03_cancelledReservasDoNotTakePlaces` | `ReservaPersistenceAdapterTest.US03_CA03_countsReservasByClaseAndEstado` | `ReservaControllerReservarTest.US03_CA03_rejectsWhenClaseIsFull` |
| CA-04 Reserva solapada | INV-12 | 409 `RESERVA_SOLAPADA` | — | `ReservarPlazaServiceTest.US03_CA04_rejectsOverlappingReservaOfSameSocio` | `ReservaPersistenceAdapterTest.US03_CA04_detectsActiveReservaOfSocioAtSameStartTime`, `US03_CA04_ignoresCancelledReservasWhenCheckingOverlap` | `ReservaControllerReservarTest.US03_CA04_rejectsOverlappingReserva` |
| CA-05 Clase ya comenzada | INV-10 | 409 `CLASE_YA_COMENZADA` | — | `ReservarPlazaServiceTest.US03_CA05_rejectsClaseThatAlreadyStarted`, `US03_CA05_rejectsClaseStartingRightNow` | — | `ReservaControllerReservarTest.US03_CA05_rejectsClaseThatAlreadyStarted` |

US03 CA-02 se prueba con un socio `VENCIDA` creado con `Socio.restore`, porque en V1 no hay API para
cambiar la membresía.

## RF-04 · US04 Cancelar reserva · `cancelarReserva`

`POST /api/v1/reservas/{reservaId}/cancelar` → `CancelarReservaService` (INV-13 e INV-14 en `Reserva.cancelar()`)

| CA | Invariante | Error | Dominio | Aplicación | Persistencia | Web |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| CA-01 Cancela y libera la plaza | — | — | `US04CancelarReservaTest.US04_CA01_cancelsReservaWithAtLeastTwoHoursInAdvance`, `US04_CA01_cancelsReservaExactlyTwoHoursInAdvance` | `CancelarReservaServiceTest.US04_CA01_cancelsReservaWithAtLeastTwoHoursInAdvance`, `US04_CA01_freesThePlaceOfTheClase` | — | `ReservaControllerCancelarTest.US04_CA01_cancelsReserva` |
| CA-02 Menos de 2 h | INV-14 | 409 `CANCELACION_FUERA_DE_PLAZO` | `US04CancelarReservaTest.US04_CA02_rejectsCancellationWithLessThanTwoHoursInAdvance`, `US04_CA02_rejectsCancellationOfAClassThatAlreadyStarted` | `CancelarReservaServiceTest.US04_CA02_rejectsCancellationWithLessThanTwoHoursInAdvance` | — | `ReservaControllerCancelarTest.US04_CA02_rejectsCancellationWithLessThanTwoHoursInAdvance` |
| CA-03 Ya cancelada | INV-13 | 409 `INVALID_STATE_TRANSITION` | `US04CancelarReservaTest.US04_CA03_rejectsCancellationOfAlreadyCancelledReserva`, `US04_CA03_rejectsCancellingTwice` | `CancelarReservaServiceTest.US04_CA03_rejectsCancellationOfAlreadyCancelledReserva` | — | `ReservaControllerCancelarTest.US04_CA03_rejectsCancellationOfAlreadyCancelledReserva` |

INV-15 (una reserva cancelada no se elimina) no tiene criterio propio: se garantiza porque ningún puerto
ofrece borrado.

## RF-05 · US05 Consultar reservas y disponibilidad · `consultarReservas`, `consultarDisponibilidad`

`GET /api/v1/socios/{socioId}/reservas` → `ConsultarReservasService` ·
`GET /api/v1/clases/{claseId}/disponibilidad` → `ConsultarDisponibilidadService`

| CA | Operación | Error | Aplicación | Persistencia | Web |
| :--- | :--- | :--- | :--- | :--- | :--- |
| CA-01 Solo las reservas del socio | `consultarReservas` | — | `ConsultarReservasServiceTest.US05_CA01_returnsOnlyTheReservasOfTheSocio`, `US05_CA01_returnsEmptyPageForSocioWithoutReservas` | — | `ReservaControllerConsultarTest.US05_CA01_returnsReservasOfTheSocio` |
| CA-02 Filtro por estado | `consultarReservas` | 400 `VALIDATION_ERROR` si el estado está fuera de catálogo | `ConsultarReservasServiceTest.US05_CA02_filtersByConfirmadaState`, `US05_CA02_filtersByCanceladaState` | — | `ReservaControllerConsultarTest.US05_CA02_filtersByEstado`, `US05_CA02_rejectsEstadoOutOfCatalog` |
| CA-03 Plazas disponibles | `consultarDisponibilidad` | — | `ConsultarDisponibilidadServiceTest.US05_CA03_returnsAvailablePlaces`, `US05_CA03_returnsZeroPlacesWhenClaseIsFull` | `ReservaPersistenceAdapterTest.US05_CA03_countsConfirmadaReservasOfClaseForAvailability` | `DisponibilidadControllerTest.US05_CA03_returnsAvailablePlaces` |
| CA-04 Clase inexistente | `consultarDisponibilidad` | 404 `CLASE_NOT_FOUND` | `ConsultarDisponibilidadServiceTest.US05_CA04_rejectsUnknownClase` | — | `DisponibilidadControllerTest.US05_CA04_rejectsUnknownClase` |

US05 no tiene invariantes ni pruebas de dominio: son consultas de solo lectura.

## Pruebas sin criterio concreto

Hay 22 métodos sin prefijo `USxx_CAyy_` que cubren reglas transversales o casos de error que no
corresponden a un criterio:

| Qué cubren | Clases |
| :--- | :--- |
| Socio, clase o reserva inexistentes (404) | `ReservarPlazaServiceTest`, `CancelarReservaServiceTest`, `ConsultarReservasServiceTest`, `ReservaControllerReservarTest`, `ReservaControllerCancelarTest`, `ReservaControllerConsultarTest` |
| Entrada mal formada (400): `socioId` ausente, UUID inválido | `ReservaControllerReservarTest` |
| `restore` sin aplicar reglas, `findById` vacío, `findBySocioId` | `US02CrearClaseTest`, `US03ReservarPlazaTest`, `ClasePersistenceAdapterTest`, `ReservaPersistenceAdapterTest` |
| ADR-0004 (las 13 filas) y ADR-0007 (500 sin detalles internos) | `GlobalExceptionHandlerTest` |
| Arranque del contexto completo | `GimnasioReservasApplicationTests` |

## Referencias

El registro de cambios de las specs ([`specs/CHANGELOG.md`](../../specs/CHANGELOG.md)) documenta la
evolución de los criterios e invariantes que aparecen en esta matriz; por ejemplo, la incorporación de
US01 CA-06, US02 CA-05 y CA-06, e INV-16 e INV-17.
