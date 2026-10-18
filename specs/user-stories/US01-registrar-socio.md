# US01 - Registrar socio

**Como** Recepcionista,
**quiero** registrar un nuevo socio,
**para** que pueda reservar clases en el gimnasio.

Referencias: RF-01, `domain/catalogs.md`

## Criterios de aceptación
- **CA-01:** Dado un DNI válido de 8 dígitos, un nombre y un email válidos, cuando registro el socio,
  entonces se crea con membresía `ACTIVA`.
- **CA-02:** Dado un DNI que ya existe, cuando registro el socio, entonces se rechaza indicando que el DNI está duplicado.
- **CA-03:** Dado un DNI con formato inválido (menos de 8 dígitos, letras), cuando registro el socio,
  entonces se rechaza indicando el formato esperado.
- **CA-04:** Dado un email que ya existe, cuando registro el socio, entonces se rechaza indicando que el email está duplicado.
- **CA-05:** Dado un email con formato inválido, cuando registro el socio, entonces se rechaza.