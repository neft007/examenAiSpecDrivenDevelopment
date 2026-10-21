# reservarPlaza

> **Fuentes:** [RF-03](../../../specs/requirements/functional.md#rf-03-reservar-plaza-en-una-clase),
> [US03](../../../specs/user-stories/US03-reservar-plaza.md),
> [`architecture.md` §6](../../../specs/architecture/architecture.md#6-flujo-de-ejemplo-reservar-plaza-us03),
> [contrato](../../../specs/api/gimnasio-openapi.yaml) (`/api/v1/clases/{claseId}/reservas`),
> [`traceability.md`](../../../specs/traceability.md) y el código de `ReservaController`,
> `ReservarPlazaService`, `Reserva` y `ReservaPersistenceAdapter`.

`POST /api/v1/clases/{claseId}/reservas` · tag `Reservas` · actor: Socio

## Entrada y salida

- **Ruta:** `claseId` (UUID). **Request** `ReservarPlazaRequest`: `socioId` (UUID, obligatorio).
- **201** `ReservaResponse` en estado `CONFIRMADA`, con `fechaReserva` = instante del `Clock`.
  El contrato no declara `Location` porque no existe `GET /reservas/{id}`.
- `ReservarPlazaRequest` y `ReservaResponse` se generan a partir del contrato, y `ReservaController`
  implementa la interfaz generada `ReservasApi`. La configuración del generador está documentada en
  [`specs/resources/plugins.md`](../../../specs/resources/plugins.md).

## Flujo

Las comprobaciones se ejecutan en este orden y la primera que falla corta el flujo:

| # | Comprobación | Fallo |
| :--- | :--- | :--- |
| 1 | El socio existe | 404 `SOCIO_NOT_FOUND` |
| 2 | Membresía `ACTIVA` (INV-09) | 409 `MEMBRESIA_VENCIDA` |
| 3 | La clase existe | 404 `CLASE_NOT_FOUND` |
| 4 | La clase aún no comenzó: `fechaInicio` posterior a "ahora" (INV-10) | 409 `CLASE_YA_COMENZADA` |
| 5 | Reservas `CONFIRMADA` de la clase < `aforoMaximo` (INV-11) | 409 `AFORO_COMPLETO` |
| 6 | El socio no tiene otra reserva `CONFIRMADA` en una clase con la misma `fechaInicio` (INV-12) | 409 `RESERVA_SOLAPADA` |
| 7 | `Reserva.crear(socioId, claseId, clock)` y `save` | — |

Además, un `claseId` mal formado o un `socioId` ausente responden 400 `VALIDATION_ERROR`.

INV-09 a INV-12 viven en el servicio porque necesitan el socio, la clase y otras reservas; el agregado
`Reserva` solo crea la reserva. El solapamiento se consulta en BD uniendo `reservas` con `clases`
por `fechaInicio`.

El diagrama de secuencia de esta operación está en [diagramas.md](../diagramas.md#reservarplaza-us03).

## Limitaciones conocidas

Anotadas en [`PENDIENTES.md`](../../../PENDIENTES.md):

- No se comprueba que la clase esté `PROGRAMADA`. En V1 no puede cambiar de estado, así que basta con la fecha.
- Dos reservas simultáneas podrían saltarse INV-11 o INV-12 (no hay bloqueo).

## Pruebas

| Capa | Clase | CA cubiertos |
| :--- | :--- | :--- |
| Dominio | `US03ReservarPlazaTest` | CA-01 (estado `CONFIRMADA`, ids únicos) |
| Aplicación | `ReservarPlazaServiceTest` | CA-01 a CA-05, más socio y clase inexistentes |
| Persistencia | `ReservaPersistenceAdapterTest` | CA-01, CA-03 (conteo), CA-04 (solapamiento, ignora canceladas) |
| Web | `ReservaControllerReservarTest` | CA-01 a CA-05, más clase inexistente, `socioId` ausente y UUID mal formado |

US03 CA-02 se prueba con un socio `VENCIDA` creado con `Socio.restore`, porque en V1 no hay API para
cambiar la membresía ([`traceability.md`](../../../specs/traceability.md)).
