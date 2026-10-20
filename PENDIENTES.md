# Pendientes

Lista de tareas futuras del proyecto.

- [x] Cuando se implemente GlobalExceptionHandler, asegurarse de que captura MethodArgumentNotValidException y responde 400 VALIDATION_ERROR (ADR-0004), para los errores de @Pattern del DTO generado.
- [ ] Corregir y registrar ADR-0007 (`specs/adr/0007-error-interno-generico.md`): la Decisión menciona un enum `ErrorCode` que el contrato no tiene, y falta añadirlo a `specs/adr/README.md`.
- [ ] Crear `SocioNotFoundException` (404 `SOCIO_NOT_FOUND`) y su manejo en GlobalExceptionHandler cuando lo necesite una historia (US03, US05).
- [ ] Completar GlobalExceptionHandlerTest con el resto de filas de ADR-0004 a medida que se creen sus excepciones.
