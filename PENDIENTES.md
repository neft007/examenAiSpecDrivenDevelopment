# Pendientes

Lista de tareas futuras del proyecto.

- [x] Cuando se implemente GlobalExceptionHandler, asegurarse de que captura MethodArgumentNotValidException y responde 400 VALIDATION_ERROR (ADR-0004), para los errores de @Pattern del DTO generado.
- [x] Corregir y registrar ADR-0007 (`specs/adr/0007-error-interno-generico.md`): la Decisión menciona un enum `ErrorCode` que el contrato no tiene, y falta añadirlo a `specs/adr/README.md`.
- [x] Crear `SocioNotFoundException` (404 `SOCIO_NOT_FOUND`) y su manejo en GlobalExceptionHandler cuando lo necesite una historia (US03, US05).
- [x] Completar GlobalExceptionHandlerTest con el resto de filas de ADR-0004 a medida que se creen sus excepciones.
- [ ] Validar que la clase esté en estado PROGRAMADA antes de reservar. En V1 no hay forma de cancelar una clase, así que no puede pasar, pero debería especificarse si se añade Clase.cancelar.
- [ ] Race condition en INV-11 e INV-12: dos reservas simultáneas pueden saltarse el aforo o el solapamiento. Se resolvería con bloqueo optimista o pesimista en una versión futura. Aceptable para H2 en un proyecto de aprendizaje.
- [ ] Consultar el detalle de una clase (RF-05): no tiene operación en el contrato ni historia que la cubra. Fuera del alcance de V1. Si se necesita, añadir GET /clases/{id} a la spec, al contrato y a una historia.
- [ ] Una ruta inexistente responde 500 en lugar de 404. ADR-0007 dice que toda excepción no mapeada responde 500, pero una ruta inexistente no es un error del servidor. Se resolvería ampliando ADR-0004 y ADR-0007 con un código específico (ej. ROUTE_NOT_FOUND). Fuera del alcance de V1.
- [ ] Implementar prueba de arquitectura con ArchUnit para verificar la regla de dependencia (ADR-0001). Fuera del alcance de V1.
- [ ] Implementar Clase.cancelar y Clase.completar cuando haya una historia que los requiera (fuera del alcance de V1).
