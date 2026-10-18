# US03 - Reservar plaza en una clase

**Como** Socio,
**quiero** reservar una plaza en una clase,
**para** asegurar mi asistencia.

Referencias: RF-03, `domain/lifecycle.md`

## Criterios de aceptación
- **CA-01:** Dado un socio con membresía `ACTIVA`, una clase `PROGRAMADA` con plazas disponibles
  y sin otra reserva en el mismo horario, cuando reservo, entonces se crea una reserva `CONFIRMADA`.
- **CA-02:** Dado un socio con membresía `VENCIDA`, cuando intento reservar, entonces se rechaza.
- **CA-03:** Dada una clase sin plazas disponibles, cuando intento reservar, entonces se rechaza.
- **CA-04:** Dado un socio que ya tiene una reserva en el mismo horario, cuando intento reservar, entonces se rechaza.
- **CA-05:** Dada una clase que ya comenzó, cuando intento reservar, entonces se rechaza.