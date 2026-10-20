# CLAUDE.md - Sistema de Reservas para Gimnasio

Proyecto desarrollado con **Spec-Driven Development (SDD)**. La carpeta `specs/` es la fuente única
de verdad: nada se implementa si no está especificado primero.

## Fuentes de verdad (leer antes de implementar)
1. `specs/vision.md` - alcance y fuera de alcance.
2. `specs/requirements/functional.md` - RF-01 a RF-05.
3. `specs/domain/lifecycle.md` y `specs/domain/catalogs.md` - estados y catálogos.
4. `specs/user-stories/` - criterios de aceptación (CA) por historia.
5. `specs/architecture/domain-model.md` - invariantes.
6. `specs/architecture/architecture.md` - capas, puertos, modelos por frontera y paquetes.
7. `specs/adr/` - decisiones aceptadas; no contradecirlas.
8. `specs/api/gimnasio-openapi.yaml` - contrato autoritativo.
9. `specs/traceability.md` - operación -> caso de uso -> historia -> invariantes -> pruebas.

## Reglas obligatorias
1. **Si la spec no alcanza, DETENTE.** No inventes campos, reglas, estados, rutas ni códigos de error.
2. **Contrato 1:1:** el controlador implementa la interfaz generada correspondiente a su tag (`SociosApi`, `ClasesApi`, `ReservasApi`).
3. **Arquitectura hexagonal (ADR-0001):**
   - `domain`: Java puro. Sin Spring, sin JPA, sin puertos. Las reglas viven aquí.
   - `application.port.in`: interfaces `*UseCase` + Commands, Queries y Results (`record`).
   - `application.port.out`: `SocioPersistencePort`, `ClasePersistencePort`, `ReservaPersistencePort`.
   - `application.service`: un servicio por caso de uso; orquesta, no contiene reglas de negocio.
   - `adapter.in.web`: solo traduce HTTP <-> Command/Result y excepciones -> ErrorResponse.
   - `adapter.out.persistence`: implementa los puertos con JPA.
4. **El dominio nunca sale de `application`:** el controlador recibe `*Result`, nunca entidades de dominio.
5. **Enums** idénticos a `specs/domain/` (mismos valores, mismo orden).
6. **Errores** exactamente como ADR-0004 (HTTP + `code`).
7. **Pruebas:** al menos una por criterio de aceptación, con el nombre
   `USxx_CAyy_descripcionEnIngles`.
8. **Idioma (`specs/conventions.md`):** código en inglés; documentación en castellano.
9. **Antes de cada commit:** `mvn test` debe pasar.
10. **Commits:** prefijos `spec:`, `design:`, `contract:`, `tooling:`, `feat:`, `test:`, `fix:`, `refactor:`, `docs:`.