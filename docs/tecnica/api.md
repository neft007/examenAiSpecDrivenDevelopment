# API REST

> **Fuentes:** [`specs/api/gimnasio-openapi.yaml`](../../specs/api/gimnasio-openapi.yaml) (contrato autoritativo),
> [ADR-0002](../../specs/adr/0002-transiciones-como-acciones.md),
> [ADR-0003](../../specs/adr/0003-identificador-uuid.md),
> [ADR-0004](../../specs/adr/0004-codigos-de-error.md),
> [ADR-0007](../../specs/adr/0007-error-interno-generico.md) y
> `adapter/in/web/GlobalExceptionHandler.java`.

OpenAPI 3.0.3, versión `1.0.0`, servidor local `http://localhost:8080`. El contrato se puede explorar
en Swagger UI (`/swagger-ui.html`).

Las interfaces de la API (`SociosApi`, `ClasesApi`, `ReservasApi`) se generan a partir del contrato; la
configuración del generador está documentada en [`specs/resources/plugins.md`](../../specs/resources/plugins.md).

## Operaciones

| Método y ruta | operationId | Tag | Éxito | Errores | Historia |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST /api/v1/socios` | `registrarSocio` | Socios | 201 `SocioResponse` + `Location` | 400, 409, 500 | [US01](operaciones/registrarSocio.md) |
| `POST /api/v1/clases` | `crearClase` | Clases | 201 `ClaseResponse` + `Location` | 400, 500 | [US02](operaciones/crearClase.md) |
| `POST /api/v1/clases/{claseId}/reservas` | `reservarPlaza` | Reservas | 201 `ReservaResponse` | 400, 404, 409, 500 | [US03](operaciones/reservarPlaza.md) |
| `POST /api/v1/reservas/{reservaId}/cancelar` | `cancelarReserva` | Reservas | 200 `ReservaResponse` | 400, 404, 409, 500 | [US04](operaciones/cancelarReserva.md) |
| `GET /api/v1/socios/{socioId}/reservas?estado=` | `consultarReservas` | Reservas | 200 `ReservaPageResponse` | 400, 404, 500 | [US05](operaciones/consultarReservas.md) |
| `GET /api/v1/clases/{claseId}/disponibilidad` | `consultarDisponibilidad` | Reservas | 200 `DisponibilidadResponse` | 400, 404, 500 | [US05](operaciones/consultarReservas.md#consultardisponibilidad) |

Convenciones del contrato:

- Los identificadores de ruta son UUID (ADR-0003).
- Las transiciones de estado son acciones `POST` con verbo de negocio (`/cancelar`), nunca `PUT`/`PATCH`
  sobre el estado (ADR-0002).
- Las fechas son `date-time` (ISO-8601); la API las devuelve en UTC.
- Cada operación lleva `x-spec-refs` con su RF y su historia.

## Esquemas de entrada

| Esquema | Campo | Restricción |
| :--- | :--- | :--- |
| `RegistrarSocioRequest` | `dni` | obligatorio, `^\d{8}$` |
| | `nombre` | obligatorio, 3–100 caracteres, sin espacios al inicio ni al final |
| | `email` | obligatorio, `format: email` |
| `CrearClaseRequest` | `nombre`, `instructor` | obligatorios, 3–100 caracteres, sin espacios al inicio ni al final |
| | `fechaInicio` | obligatoria, `date-time` (futura: la valida el dominio) |
| | `duracionMinutos` | obligatoria, 1–180 |
| | `aforoMaximo` | obligatorio, 1–50 |
| `ReservarPlazaRequest` | `socioId` | obligatorio, UUID |

Las restricciones se validan dos veces: en el DTO generado (Bean Validation) y en el dominio.
Las dos producen el mismo error `400 VALIDATION_ERROR`.

## Esquemas de salida

| Esquema | Campos |
| :--- | :--- |
| `SocioResponse` | `id`, `dni`, `nombre`, `email`, `membresia` (`EstadoMembresia`), `fechaAlta` |
| `ClaseResponse` | `id`, `nombre`, `instructor`, `fechaInicio`, `duracionMinutos`, `aforoMaximo`, `estado` (`EstadoClase`) |
| `ReservaResponse` | `id`, `socioId`, `claseId`, `fechaReserva`, `estado` (`EstadoReserva`) |
| `ReservaPageResponse` | `items` (`ReservaResponse[]`), `totalElements` |
| `DisponibilidadResponse` | `claseId`, `aforoMaximo`, `reservasConfirmadas`, `plazasDisponibles` |

Enums del contrato, idénticos a [`catalogs.md`](../../specs/domain/catalogs.md):
`EstadoMembresia [ACTIVA, VENCIDA]`, `EstadoClase [PROGRAMADA, CANCELADA, COMPLETADA]`,
`EstadoReserva [CONFIRMADA, CANCELADA]`.

## Errores

Todas las respuestas de error usan `ErrorResponse { code, message, details[] }`.

| HTTP | `code` | Cuándo |
| :--- | :--- | :--- |
| 400 | `VALIDATION_ERROR` | Formato, longitud, rango o catálogo inválidos; JSON ausente o mal formado; UUID o `estado` inválidos en la ruta o la query. |
| 404 | `SOCIO_NOT_FOUND`, `CLASE_NOT_FOUND`, `RESERVA_NOT_FOUND` | El recurso no existe. |
| 409 | `DNI_DUPLICADO`, `EMAIL_DUPLICADO` | Unicidad (INV-02, INV-03). |
| 409 | `MEMBRESIA_VENCIDA`, `CLASE_YA_COMENZADA`, `AFORO_COMPLETO`, `RESERVA_SOLAPADA` | Reglas de reserva (INV-09 a INV-12). |
| 409 | `INVALID_STATE_TRANSITION`, `CANCELACION_FUERA_DE_PLAZO` | Reglas de cancelación (INV-13, INV-14). |
| 500 | `INTERNAL_ERROR` | Cualquier excepción no mapeada. `message: "Unexpected error"`, `details: []`; la causa solo va al log (ADR-0007). |

Tabla completa con invariantes en [ADR-0004](../../specs/adr/0004-codigos-de-error.md).
En los errores de Bean Validation, `details` lleva una entrada `campo: mensaje` por cada campo inválido.

## Ejemplo

```http
POST /api/v1/socios
Content-Type: application/json

{ "dni": "12345678", "nombre": "Ana Pérez", "email": "ana@example.com" }
```

```http
HTTP/1.1 201 Created
Location: /api/v1/socios/3f6c…

{ "id": "3f6c…", "dni": "12345678", "nombre": "Ana Pérez", "email": "ana@example.com",
  "membresia": "ACTIVA", "fechaAlta": "2026-10-20T15:00:00Z" }
```
