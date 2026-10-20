# Requerimientos Funcionales - Sistema de Reservas para Gimnasio

### RF-01: Registrar socio
- Datos:
  - **DNI**: obligatorio, formato `12345678` (8 dígitos). Único en el sistema.
  - **Nombre**: obligatorio, entre 3 y 100 caracteres, sin espacios al inicio ni al final.
  - **Email**: obligatorio, formato válido de email. Único en el sistema.
  - **Fecha de alta**: se asigna automáticamente al registrar.
  - **Estado de membresía**: `ACTIVA` o `VENCIDA`. Se asigna por defecto `ACTIVA`.
- El socio se crea en estado `ACTIVO` (no confundir con membresía).

### RF-02: Crear clase
- Datos:
  - **Nombre**: obligatorio, entre 3 y 100 caracteres, sin espacios al inicio ni al final.
  - **Instructor**: obligatorio, entre 3 y 100 caracteres, sin espacios al inicio ni al final.
  - **Fecha y hora de inicio**: obligatoria, debe ser futura.
  - **Duración**: obligatoria, en minutos. Debe ser mayor a 0 y máximo 180.
  - **Aforo máximo**: obligatorio, mayor a 0 y máximo 50.
- La clase se crea en estado `PROGRAMADA`.

### RF-03: Reservar plaza en una clase
- Un socio solo puede reservar si:
  - Su membresía está `ACTIVA`.
  - La clase está `PROGRAMADA` (no ha comenzado).
  - Hay plazas disponibles (reservas activas < aforo máximo).
  - No tiene otra reserva activa en la misma fecha y hora.
- Al reservar, se crea una reserva en estado `CONFIRMADA`.
- La reserva tiene un código único generado por el sistema.

### RF-04: Cancelar reserva
- Un socio puede cancelar su reserva si:
  - La reserva está `CONFIRMADA`.
  - Faltan al menos 2 horas para el inicio de la clase.
- Al cancelar, la reserva pasa a estado `CANCELADA` y la plaza queda libre.

### RF-05: Consultar reservas y disponibilidad
- Consultar las reservas de un socio (con filtro opcional por estado).
- Consultar las plazas disponibles de una clase (aforo máximo - reservas confirmadas).
- Consultar el detalle de una clase.