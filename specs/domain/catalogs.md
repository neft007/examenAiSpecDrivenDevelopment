# Catálogos Cerrados

Un valor fuera de estos catálogos debe ser rechazado.

## 1. Estado de membresía del socio
| Código | Descripción |
| :--- | :--- |
| `ACTIVA` | Membresía vigente. |
| `VENCIDA` | Membresía vencida. |

## 2. Estado de la clase
| Código | Descripción |
| :--- | :--- |
| `PROGRAMADA` | Clase programada y disponible. |
| `CANCELADA` | Clase cancelada. |
| `COMPLETADA` | Clase ya realizada. |

## 3. Estado de la reserva
| Código | Descripción |
| :--- | :--- |
| `CONFIRMADA` | Reserva activa. |
| `CANCELADA` | Reserva cancelada por el socio. |

Estos catálogos se reflejan, con los mismos valores y en el mismo orden, en los enums del dominio
(`Membresia`, `EstadoClase`, `EstadoReserva`) y en los del contrato (`EstadoMembresia`, `EstadoClase`,
`EstadoReserva`). Los del contrato los genera el generador OpenAPI en `adapter.in.web.generated.model`
(configuración en [`resources/plugins.md`](../resources/plugins.md)). Un valor fuera de catálogo en la
entrada (por ejemplo, el filtro `estado` de `consultarReservas`) no se puede convertir a ese enum, y la API
responde `400 VALIDATION_ERROR` (ADR-0004).
