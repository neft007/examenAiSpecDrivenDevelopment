# Diagramas

> **Fuentes:** [`architecture.md` §2 y §6](../../specs/architecture/architecture.md#6-flujo-de-ejemplo-reservar-plaza-us03),
> [`gimnasio-openapi.yaml`](../../specs/api/gimnasio-openapi.yaml),
> [ADR-0004](../../specs/adr/0004-codigos-de-error.md), [ADR-0007](../../specs/adr/0007-error-interno-generico.md),
> las historias de usuario (actores) y el código de los `*Controller`, `*Service`, `*PersistencePort`,
> `Socio`, `Clase` y `Reserva`.

Un diagrama de secuencia por `operationId`, en el estilo del flujo de ejemplo de
[`architecture.md` §6](../../specs/architecture/architecture.md#6-flujo-de-ejemplo-reservar-plaza-us03), y un
diagrama de componentes adaptador → puerto → servicio. Complementan [arquitectura.md](arquitectura.md) (capas y
puertos) y [dominio.md](dominio.md) (invariantes).

> **Si hay contradicción, manda la spec.** Estos diagramas se derivan de las specs y del código; no añaden reglas.
> Una diferencia entre un diagrama y la spec se anota en [`sync-report.md`](../sync-report.md).

**Convenciones de los diagramas:**

- `UC` es la interfaz del caso de uso (`application.port.in`) y `SVC` su implementación.
- `H` es `GlobalExceptionHandler`: traduce cada excepción al HTTP y `code` de ADR-0004. Una excepción no
  mapeada responde `500 INTERNAL_ERROR` (ADR-0007).
- La primera rama `alt` de cada diagrama es la validación de la petición antes de llegar al controlador: los DTO
  generados llevan anotaciones de Bean Validation, y Spring convierte los parámetros de ruta y consulta
  (UUID, enums). Si falla, responde `400 VALIDATION_ERROR`.
- Las comprobaciones aparecen en el mismo orden que en el código.

| Operación | Historia | Actor | Diagrama |
| :--- | :--- | :--- | :--- |
| `registrarSocio` | US01 | Recepcionista | [ver](#registrarsocio-us01) |
| `crearClase` | US02 | Administrador | [ver](#crearclase-us02) |
| `reservarPlaza` | US03 | Socio | [ver](#reservarplaza-us03) |
| `cancelarReserva` | US04 | Socio | [ver](#cancelarreserva-us04) |
| `consultarReservas` | US05 | Socio | [ver](#consultarreservas-us05) |
| `consultarDisponibilidad` | US05 | Socio | [ver](#consultardisponibilidad-us05) |

## Componentes: adaptador → puerto → servicio

```mermaid
flowchart LR
    subgraph IN["adapter.in.web"]
        SC["SocioController"]
        CC["ClaseController"]
        RC["ReservaController"]
    end
    subgraph PIN["application.port.in"]
        RSU["RegistrarSocioUseCase"]
        CCU["CrearClaseUseCase"]
        RPU["ReservarPlazaUseCase"]
        CRU["CancelarReservaUseCase"]
        QRU["ConsultarReservasUseCase"]
        QDU["ConsultarDisponibilidadUseCase"]
    end
    subgraph SVC["application.service"]
        RSS["RegistrarSocioService"]
        CCS["CrearClaseService"]
        RPS["ReservarPlazaService"]
        CRS["CancelarReservaService"]
        QRS["ConsultarReservasService"]
        QDS["ConsultarDisponibilidadService"]
    end
    subgraph POUT["application.port.out"]
        SPP["SocioPersistencePort"]
        CPP["ClasePersistencePort"]
        RPP["ReservaPersistencePort"]
    end
    subgraph OUT["adapter.out.persistence"]
        SPA["SocioPersistenceAdapter"]
        CPA["ClasePersistenceAdapter"]
        RPA["ReservaPersistenceAdapter"]
    end
    DB[("H2")]

    SC --> RSU
    CC --> CCU
    RC --> RPU & CRU & QRU & QDU
    RSU -. implementa .-> RSS
    CCU -. implementa .-> CCS
    RPU -. implementa .-> RPS
    CRU -. implementa .-> CRS
    QRU -. implementa .-> QRS
    QDU -. implementa .-> QDS
    RSS --> SPP
    CCS --> CPP
    RPS --> SPP & CPP & RPP
    CRS --> RPP & CPP
    QRS --> SPP & RPP
    QDS --> CPP & RPP
    SPP -. implementa .-> SPA
    CPP -. implementa .-> CPA
    RPP -. implementa .-> RPA
    SPA & CPA & RPA --> DB
```

Las flechas continuas son "usa" y las punteadas "implementa". Los controladores solo conocen las interfaces
`*UseCase`, y los servicios solo los `*PersistencePort` (regla de dependencia de
[`architecture.md` §2](../../specs/architecture/architecture.md#2-capas-y-regla-de-dependencia)). Todos los
controladores comparten `GlobalExceptionHandler`, y todos los servicios que fijan fechas reciben el `Clock` de
`ClockConfiguration`.

## registrarSocio (US01)

`POST /api/v1/socios` → `201 SocioResponse` con `Location`.

```mermaid
sequenceDiagram
    autonumber
    actor A as Recepcionista
    participant C as SocioController
    participant H as GlobalExceptionHandler
    participant UC as RegistrarSocioUseCase
    participant SVC as RegistrarSocioService
    participant D as Dni / Email / Socio
    participant SP as SocioPersistencePort

    A->>C: POST /api/v1/socios
    alt petición inválida (formato, longitud)
        C-->>H: MethodArgumentNotValidException
        H-->>A: 400 VALIDATION_ERROR
    else petición válida
        C->>UC: registrar(RegistrarSocioCommand)
        UC->>SVC: (implementación)
        SVC->>D: new Dni(dni), new Email(email)
        alt DNI o email con formato inválido
            D-->>H: ValidationException (INV-01, INV-04)
            H-->>A: 400 VALIDATION_ERROR
        else formato válido
            SVC->>SP: existsByDni(dni)
            alt DNI ya registrado
                SP-->>SVC: true
                SVC-->>H: DniDuplicadoException (INV-02)
                H-->>A: 409 DNI_DUPLICADO
            else DNI libre
                SVC->>SP: existsByEmail(email)
                alt email ya registrado
                    SP-->>SVC: true
                    SVC-->>H: EmailDuplicadoException (INV-03)
                    H-->>A: 409 EMAIL_DUPLICADO
                else email libre
                    SVC->>D: Socio.registrar(dni, nombre, email, clock)
                    alt nombre inválido
                        D-->>H: ValidationException (INV-05)
                        H-->>A: 400 VALIDATION_ERROR
                    else nombre válido
                        D-->>SVC: Socio (membresía ACTIVA)
                        SVC->>SP: save(socio)
                        SP-->>SVC: Socio
                        SVC-->>C: SocioResult
                        C-->>A: 201 SocioResponse + Location
                    end
                end
            end
        end
    end
```

Si dos registros simultáneos superan `existsByDni`/`existsByEmail`, la restricción única de la base de datos
hace fallar `save`, y `SocioPersistenceAdapter` la traduce a `DniDuplicadoException` o
`EmailDuplicadoException` (409).

## crearClase (US02)

`POST /api/v1/clases` → `201 ClaseResponse` con `Location`.

```mermaid
sequenceDiagram
    autonumber
    actor A as Administrador
    participant C as ClaseController
    participant H as GlobalExceptionHandler
    participant UC as CrearClaseUseCase
    participant SVC as CrearClaseService
    participant D as Clase
    participant CP as ClasePersistencePort

    A->>C: POST /api/v1/clases
    alt petición inválida (formato, longitud, rango)
        C-->>H: MethodArgumentNotValidException
        H-->>A: 400 VALIDATION_ERROR
    else petición válida
        C->>UC: crear(CrearClaseCommand)
        UC->>SVC: (implementación)
        SVC->>D: Clase.crear(nombre, instructor, fechaInicio, duracionMinutos, aforoMaximo, clock)
        alt nombre, instructor, fecha, duración o aforo inválidos
            D-->>H: ValidationException (INV-16, INV-17, INV-06, INV-08, INV-07)
            H-->>A: 400 VALIDATION_ERROR
        else datos válidos
            D-->>SVC: Clase (PROGRAMADA)
            SVC->>CP: save(clase)
            CP-->>SVC: Clase
            SVC-->>C: ClaseResult
            C-->>A: 201 ClaseResponse + Location
        end
    end
```

`Clase.crear` comprueba las invariantes en el orden del diagrama: nombre (INV-16), instructor (INV-17), fecha
futura (INV-06), duración (INV-08) y aforo (INV-07).

## reservarPlaza (US03)

`POST /api/v1/clases/{claseId}/reservas` → `201 ReservaResponse` (sin `Location`).

Es el flujo de [`architecture.md` §6](../../specs/architecture/architecture.md#6-flujo-de-ejemplo-reservar-plaza-us03),
con la validación de la petición añadida al principio.

```mermaid
sequenceDiagram
    autonumber
    actor S as Socio
    participant C as ReservaController
    participant H as GlobalExceptionHandler
    participant UC as ReservarPlazaUseCase
    participant SVC as ReservarPlazaService
    participant SP as SocioPersistencePort
    participant CP as ClasePersistencePort
    participant RP as ReservaPersistencePort
    participant R as Reserva

    S->>C: POST /api/v1/clases/{claseId}/reservas
    alt claseId mal formado o socioId ausente
        C-->>H: MethodArgumentTypeMismatchException / MethodArgumentNotValidException
        H-->>S: 400 VALIDATION_ERROR
    else petición válida
        C->>UC: reservar(ReservarPlazaCommand)
        UC->>SVC: (implementación)
        SVC->>SP: findById(socioId)
        alt socio no existe
            SP-->>SVC: vacío
            SVC-->>H: SocioNotFoundException
            H-->>S: 404 SOCIO_NOT_FOUND
        else socio existe
            SP-->>SVC: Socio
            alt membresía VENCIDA
                SVC-->>H: MembresiaVencidaException (INV-09)
                H-->>S: 409 MEMBRESIA_VENCIDA
            else membresía ACTIVA
                SVC->>CP: findById(claseId)
                alt clase no existe
                    CP-->>SVC: vacío
                    SVC-->>H: ClaseNotFoundException
                    H-->>S: 404 CLASE_NOT_FOUND
                else clase existe
                    CP-->>SVC: Clase
                    alt clase ya comenzó
                        SVC-->>H: ClaseYaComenzadaException (INV-10)
                        H-->>S: 409 CLASE_YA_COMENZADA
                    else clase futura
                        SVC->>RP: countByClaseIdAndEstado(claseId, CONFIRMADA)
                        alt aforo completo
                            RP-->>SVC: count >= aforo
                            SVC-->>H: AforoCompletoException (INV-11)
                            H-->>S: 409 AFORO_COMPLETO
                        else hay plazas
                            SVC->>RP: existsBySocioIdAndFechaInicio(socioId, fechaInicio)
                            alt ya tiene reserva en ese horario
                                RP-->>SVC: true
                                SVC-->>H: ReservaSolapadaException (INV-12)
                                H-->>S: 409 RESERVA_SOLAPADA
                            else no hay solapamiento
                                SVC->>R: Reserva.crear(socioId, claseId, clock)
                                R-->>SVC: Reserva (CONFIRMADA)
                                SVC->>RP: save(reserva)
                                RP-->>SVC: Reserva
                                SVC-->>C: ReservaResult
                                C-->>S: 201 ReservaResponse
                            end
                        end
                    end
                end
            end
        end
    end
```

INV-09 a INV-12 viven en el servicio porque necesitan el socio, la clase y otras reservas; `Reserva` solo se
crea en estado `CONFIRMADA`.

## cancelarReserva (US04)

`POST /api/v1/reservas/{reservaId}/cancelar` → `200 ReservaResponse` (acción de negocio, ADR-0002).

```mermaid
sequenceDiagram
    autonumber
    actor S as Socio
    participant C as ReservaController
    participant H as GlobalExceptionHandler
    participant UC as CancelarReservaUseCase
    participant SVC as CancelarReservaService
    participant RP as ReservaPersistencePort
    participant CP as ClasePersistencePort
    participant R as Reserva

    S->>C: POST /api/v1/reservas/{reservaId}/cancelar
    alt reservaId mal formado
        C-->>H: MethodArgumentTypeMismatchException
        H-->>S: 400 VALIDATION_ERROR
    else petición válida
        C->>UC: cancelar(CancelarReservaCommand)
        UC->>SVC: (implementación)
        SVC->>RP: findById(reservaId)
        alt reserva no existe
            RP-->>SVC: vacío
            SVC-->>H: ReservaNotFoundException
            H-->>S: 404 RESERVA_NOT_FOUND
        else reserva existe
            RP-->>SVC: Reserva
            SVC->>CP: findById(claseId de la reserva)
            alt clase no existe (error de integridad)
                CP-->>SVC: vacío
                SVC-->>H: IllegalStateException
                H-->>S: 500 INTERNAL_ERROR (ADR-0007)
            else clase existe
                CP-->>SVC: Clase
                SVC->>R: cancelar(fechaInicioClase, clock)
                alt reserva no CONFIRMADA
                    R-->>H: InvalidStateTransitionException (INV-13)
                    H-->>S: 409 INVALID_STATE_TRANSITION
                else menos de 2 horas de antelación
                    R-->>H: CancelacionFueraDePlazoException (INV-14)
                    H-->>S: 409 CANCELACION_FUERA_DE_PLAZO
                else cancelación válida
                    R-->>SVC: estado CANCELADA
                    SVC->>RP: save(reserva)
                    RP-->>SVC: Reserva
                    SVC-->>C: ReservaResult
                    C-->>S: 200 ReservaResponse
                end
            end
        end
    end
```

INV-13 e INV-14 viven en `Reserva.cancelar`; el servicio solo aporta la fecha de inicio de la clase, que la
reserva no conoce. La reserva cancelada se guarda, nunca se borra (INV-15), y `fechaReserva` no cambia.

## consultarReservas (US05)

`GET /api/v1/socios/{socioId}/reservas?estado=` → `200 ReservaPageResponse`.

```mermaid
sequenceDiagram
    autonumber
    actor S as Socio
    participant C as ReservaController
    participant H as GlobalExceptionHandler
    participant UC as ConsultarReservasUseCase
    participant SVC as ConsultarReservasService
    participant SP as SocioPersistencePort
    participant RP as ReservaPersistencePort

    S->>C: GET /api/v1/socios/{socioId}/reservas?estado=
    alt socioId mal formado o estado fuera de catálogo
        C-->>H: MethodArgumentTypeMismatchException
        H-->>S: 400 VALIDATION_ERROR
    else petición válida
        C->>UC: consultar(ConsultarReservasQuery)
        UC->>SVC: (implementación)
        SVC->>SP: findById(socioId)
        alt socio no existe
            SP-->>SVC: vacío
            SVC-->>H: SocioNotFoundException
            H-->>S: 404 SOCIO_NOT_FOUND
        else socio existe
            SP-->>SVC: Socio
            SVC->>RP: findBySocioId(socioId)
            RP-->>SVC: reservas del socio
            SVC->>SVC: filtrar por estado (si se indicó)
            SVC-->>C: ReservaPageResult
            C-->>S: 200 ReservaPageResponse (items, totalElements)
        end
    end
```

Es de solo lectura y no tiene invariantes propias. Sin `estado`, devuelve todas las reservas del socio.

## consultarDisponibilidad (US05)

`GET /api/v1/clases/{claseId}/disponibilidad` → `200 DisponibilidadResponse`.

```mermaid
sequenceDiagram
    autonumber
    actor S as Socio
    participant C as ReservaController
    participant H as GlobalExceptionHandler
    participant UC as ConsultarDisponibilidadUseCase
    participant SVC as ConsultarDisponibilidadService
    participant CP as ClasePersistencePort
    participant RP as ReservaPersistencePort

    S->>C: GET /api/v1/clases/{claseId}/disponibilidad
    alt claseId mal formado
        C-->>H: MethodArgumentTypeMismatchException
        H-->>S: 400 VALIDATION_ERROR
    else petición válida
        C->>UC: consultar(ConsultarDisponibilidadQuery)
        UC->>SVC: (implementación)
        SVC->>CP: findById(claseId)
        alt clase no existe
            CP-->>SVC: vacío
            SVC-->>H: ClaseNotFoundException
            H-->>S: 404 CLASE_NOT_FOUND
        else clase existe
            CP-->>SVC: Clase
            SVC->>RP: countByClaseIdAndEstado(claseId, CONFIRMADA)
            RP-->>SVC: reservasConfirmadas
            SVC->>SVC: plazasDisponibles = aforoMaximo - reservasConfirmadas
            SVC-->>C: DisponibilidadResult
            C-->>S: 200 DisponibilidadResponse
        end
    end
```

Cuenta solo las reservas `CONFIRMADA`, igual que la comprobación de aforo de `reservarPlaza` (INV-11).
