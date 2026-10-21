# consultarReservas y consultarDisponibilidad

> **Fuentes:** [RF-05](../../../specs/requirements/functional.md#rf-05-consultar-reservas-y-disponibilidad),
> [US05](../../../specs/user-stories/US05-consultar-reservas.md),
> [contrato](../../../specs/api/gimnasio-openapi.yaml) (`/api/v1/socios/{socioId}/reservas`,
> `/api/v1/clases/{claseId}/disponibilidad`), [`traceability.md`](../../../specs/traceability.md)
> y el código de `ReservaController`, `ConsultarReservasService` y `ConsultarDisponibilidadService`.

US05 tiene dos operaciones; las dos se documentan aquí. Ambas son de solo lectura
(`@Transactional(readOnly = true)`) y no tienen invariantes propias.

`ReservaPageResponse`, `DisponibilidadResponse` y el enum `EstadoReserva` del filtro `estado` se generan a
partir del contrato, y `ReservaController` implementa las dos operaciones de la interfaz generada `ReservasApi`.
La configuración del generador está documentada en [`specs/resources/plugins.md`](../../../specs/resources/plugins.md).

## consultarReservas

`GET /api/v1/socios/{socioId}/reservas?estado={CONFIRMADA|CANCELADA}` · tag `Reservas` · actor: Socio

- **Entrada:** `socioId` (UUID, ruta), `estado` (opcional, `EstadoReserva`). Sin `estado` no se filtra.
- **200** `ReservaPageResponse { items, totalElements }`. `totalElements` es el número de elementos
  devueltos; el contrato no define paginación.

```text
ConsultarReservasService:
  findById(socioId)        → 404 SOCIO_NOT_FOUND
  findBySocioId(socioId)   → filtro por estado si se indica → ReservaPageResult
```

El diagrama de secuencia de esta operación está en [diagramas.md](../diagramas.md#consultarreservas-us05).

| Caso | Respuesta | CA |
| :--- | :--- | :--- |
| Devuelve solo las reservas del socio (vacío si no tiene) | 200 | CA-01 |
| Filtra por `estado` | 200 | CA-02 |
| `estado` fuera de catálogo o UUID mal formado | 400 `VALIDATION_ERROR` | — |
| Socio inexistente | 404 `SOCIO_NOT_FOUND` | — |

## consultarDisponibilidad

`GET /api/v1/clases/{claseId}/disponibilidad` · tag `Reservas` · actor: Socio

- **Entrada:** `claseId` (UUID, ruta).
- **200** `DisponibilidadResponse { claseId, aforoMaximo, reservasConfirmadas, plazasDisponibles }`,
  con `plazasDisponibles = aforoMaximo − reservasConfirmadas` (RF-05).

```text
ConsultarDisponibilidadService:
  findById(claseId)                                → 404 CLASE_NOT_FOUND
  countByClaseIdAndEstado(claseId, CONFIRMADA)     → DisponibilidadResult
```

El diagrama de secuencia de esta operación está en [diagramas.md](../diagramas.md#consultardisponibilidad-us05).

| Caso | Respuesta | CA |
| :--- | :--- | :--- |
| Devuelve las plazas libres (0 si está llena; las canceladas no cuentan) | 200 | CA-03 |
| Clase inexistente | 404 `CLASE_NOT_FOUND` | CA-04 |

## Fuera de V1

RF-05 también menciona "consultar el detalle de una clase", pero no tiene operación en el contrato
ni criterio en US05. Está anotado en [`PENDIENTES.md`](../../../PENDIENTES.md).

## Pruebas

| Capa | Clase | CA cubiertos |
| :--- | :--- | :--- |
| Aplicación | `ConsultarReservasServiceTest` | CA-01, CA-02 (ambos estados), socio inexistente |
| Aplicación | `ConsultarDisponibilidadServiceTest` | CA-03 (incluye clase llena), CA-04 |
| Persistencia | `ReservaPersistenceAdapterTest` | CA-03 (conteo de confirmadas), `findBySocioId` |
| Web | `ReservaControllerConsultarTest` | CA-01, CA-02 (incluye estado fuera de catálogo), socio inexistente |
| Web | `DisponibilidadControllerTest` | CA-03, CA-04 |
