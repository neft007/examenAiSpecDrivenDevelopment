# ADR-0003: Identificador técnico UUID

- **Estado:** Aceptado
- **Fecha:** 2026-10-18
- **Specs relacionadas:** RF-01, RF-02, RF-03, INV-01, INV-02

## Contexto

El DNI es único, pero podría cambiar por ley o por errores administrativos. Además, exponer un identificador numérico autoincremental revela el volumen de socios, clases o reservas.

## Decisión

Cada entidad (`Socio`, `Clase`, `Reserva`) tiene un `id` UUID generado por el sistema, usado en las rutas (`/socios/{socioId}`, `/clases/{claseId}`, `/reservas/{reservaId}`). El DNI es un atributo de negocio con restricción de unicidad.

## Alternativas consideradas

| Alternativa | A favor | En contra |
| :--- | :--- | :--- |
| DNI como identificador en la ruta | URLs legibles. | Acopla las rutas a una regla de negocio que podría cambiar. |
| ID numérico autoincremental | Simple. | Expone volumen del gimnasio; depende de la base de datos para generarse. |

## Consecuencias

- El dominio puede crear el `id` sin depender de la base de datos.
- La búsqueda por DNI, si se necesita, será un filtro del listado y no una ruta.
- Los UUID son opacos para el usuario, pero seguros y únicos.