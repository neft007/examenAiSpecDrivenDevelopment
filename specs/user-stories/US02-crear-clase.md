# US02 - Crear clase

**Como** Administrador,
**quiero** crear una clase con horario y aforo,
**para** que los socios puedan reservar plazas.

Referencias: RF-02, `domain/catalogs.md`

## Criterios de aceptación
- **CA-01:** Dados datos válidos (nombre, instructor, fecha futura, duración y aforo),
  cuando creo la clase, entonces se crea en estado `PROGRAMADA`.
- **CA-02:** Dada una fecha de inicio en el pasado, cuando creo la clase, entonces se rechaza.
- **CA-03:** Dado un aforo mayor a 50 o menor a 1, cuando creo la clase, entonces se rechaza.
- **CA-04:** Dada una duración mayor a 180 o menor a 1, cuando creo la clase, entonces se rechaza.