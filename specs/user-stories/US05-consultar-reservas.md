# US05 - Consultar reservas y disponibilidad

**Como** Socio,
**quiero** consultar mis reservas y las plazas disponibles de una clase,
**para** planificar mi asistencia.

Referencias: RF-05

## Criterios de aceptación
- **CA-01:** Dado un socio, cuando consulto sus reservas, entonces obtengo solo las reservas de ese socio.
- **CA-02:** Dado un socio con reservas, cuando consulto sus reservas filtradas por estado `CONFIRMADA`,
  entonces obtengo solo las confirmadas.
- **CA-03:** Dada una clase, cuando consulto su disponibilidad, entonces obtengo el número de plazas libres.
- **CA-04:** Dado un identificador de clase inexistente, cuando consulto, entonces se informa que la clase no existe.