# US04 - Cancelar reserva

**Como** Socio,
**quiero** cancelar una reserva,
**para** liberar mi plaza si no puedo asistir.

Referencias: RF-04, `domain/lifecycle.md`

## Criterios de aceptación
- **CA-01:** Dada una reserva `CONFIRMADA` con al menos 2 horas de antelación, cuando cancelo,
  entonces la reserva pasa a `CANCELADA` y la plaza queda libre.
- **CA-02:** Dada una reserva `CONFIRMADA` con menos de 2 horas de antelación, cuando cancelo,
  entonces se rechaza.
- **CA-03:** Dada una reserva ya `CANCELADA`, cuando intento cancelar, entonces se rechaza.