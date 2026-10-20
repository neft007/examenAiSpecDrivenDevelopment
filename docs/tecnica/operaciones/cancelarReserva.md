# cancelarReserva

> **Fuentes:** [RF-04](../../../specs/requirements/functional.md#rf-04-cancelar-reserva),
> [US04](../../../specs/user-stories/US04-cancelar-reserva.md),
> [`lifecycle.md`](../../../specs/domain/lifecycle.md),
> [ADR-0002](../../../specs/adr/0002-transiciones-como-acciones.md),
> [contrato](../../../specs/api/gimnasio-openapi.yaml) (`/api/v1/reservas/{reservaId}/cancelar`,
> `x-transition: CONFIRMADA -> CANCELADA`) y el código de `ReservaController`,
> `CancelarReservaService` y `Reserva`.

`POST /api/v1/reservas/{reservaId}/cancelar` · tag `Reservas` · actor: Socio

## Entrada y salida

- **Ruta:** `reservaId` (UUID). Sin cuerpo.
- **200** `ReservaResponse` en estado `CANCELADA`. `fechaReserva` no cambia.

Es una acción de negocio (ADR-0002): el estado nunca se edita con `PUT`/`PATCH`.

## Flujo

```text
ReservaController → ReservaWebMapper.toCancelarCommand → CancelarReservaUseCase.cancelar
  CancelarReservaService:
    findById(reservaId)                     → 404 RESERVA_NOT_FOUND
    clase = findById(reserva.claseId)       → 500 si falta (error de integridad)
    reserva.cancelar(clase.fechaInicio, clock)
      INV-13  estado == CONFIRMADA          → 409 INVALID_STATE_TRANSITION
      INV-14  faltan ≥ 2 h para el inicio   → 409 CANCELACION_FUERA_DE_PLAZO
    save
  → ReservaResult → 200
```

Las dos reglas viven en `Reserva.cancelar()`. El servicio solo aporta la fecha de inicio de la clase,
que la reserva no conoce.

## Reglas y errores

| Regla | Invariante | Respuesta | CA |
| :--- | :--- | :--- | :--- |
| Solo se cancela una reserva `CONFIRMADA` | INV-13 | 409 `INVALID_STATE_TRANSITION` | CA-03 |
| Al menos 2 h de antelación (exactamente 2 h se admite) | INV-14 | 409 `CANCELACION_FUERA_DE_PLAZO` | CA-02 |
| La reserva cancelada se conserva | INV-15 | — | — |
| UUID mal formado | — | 400 `VALIDATION_ERROR` | — |

**La plaza queda libre** (CA-01) sin código específico: el aforo y la disponibilidad solo cuentan
reservas `CONFIRMADA`.

## Pruebas

| Capa | Clase | CA cubiertos |
| :--- | :--- | :--- |
| Dominio | `US04CancelarReservaTest` | CA-01 (incluye exactamente 2 h), CA-02 (incluye clase ya empezada), CA-03 |
| Aplicación | `CancelarReservaServiceTest` | CA-01 (incluye que la plaza queda libre), CA-02, CA-03, reserva inexistente |
| Web | `ReservaControllerCancelarTest` | CA-01 a CA-03, reserva inexistente |
