# Ciclo de Vida de las Entidades

## 1. Socio (Estado de membresía)
| Estado | Código | Descripción |
| :--- | :--- | :--- |
| Activa | `ACTIVA` | El socio puede reservar clases. |
| Vencida | `VENCIDA` | El socio no puede reservar clases. |

## 2. Clase (Estado de la clase)
| Estado | Código | Descripción |
| :--- | :--- | :--- |
| Programada | `PROGRAMADA` | La clase está disponible para reservas. |
| Cancelada | `CANCELADA` | La clase fue cancelada por el gimnasio. |
| Completada | `COMPLETADA` | La clase ya ocurrió. |

**Transiciones válidas:**
- `PROGRAMADA` -> `CANCELADA` (por el administrador).
- `PROGRAMADA` -> `COMPLETADA` (automático al pasar la fecha/hora).

## 3. Reserva (Estado de la reserva)
| Estado | Código | Descripción |
| :--- | :--- | :--- |
| Confirmada | `CONFIRMADA` | La reserva está activa. |
| Cancelada | `CANCELADA` | El socio canceló la reserva. |

**Transiciones válidas:**
- `CONFIRMADA` -> `CANCELADA` (por el socio, con al menos 2 horas de antelación).