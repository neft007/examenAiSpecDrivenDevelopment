# Prompt 02 - Refactorizar a partir de un cambio en el diseño

## Cuándo usarlo

Cuando cambia una spec (modelo de dominio, ADR, contrato o arquitectura) y el código tiene que
alinearse con ella. El cambio de spec va primero, en su propio commit; este prompt adapta el código.

## Variables

| Variable | Qué poner | Ejemplo |
| :--- | :--- | :--- |
| `{{CAMBIO}}` | Qué cambió en la spec, en una frase. | "Socio ya no expone cambiarMembresia; la membresía es final" |
| `{{SPEC_REF}}` | Archivo y sección (o commit) donde está el cambio. | `specs/architecture/domain-model.md` §4 (commit `abc1234`) |

## Prompt

```text
La spec cambió: {{CAMBIO}}. Referencia: {{SPEC_REF}}.

1. Lee {{SPEC_REF}} (y su diff con git show o git log -p si es un commit) y las specs que
   dependan de ella: domain-model.md, architecture.md, traceability.md, los ADR y el contrato.
   Si el cambio deja la spec incoherente con otra spec, DETENTE y dime dónde antes de tocar código.

2. Analiza el impacto antes de editar y muéstramelo:
   - Clases de producción afectadas, por capa (domain, application, adapters, config).
   - Pruebas afectadas, incluidos sus nombres USxx_CAyy_ si cambia el criterio que cubren.
   - Documentación derivada (docs/, README.md, PENDIENTES.md) que quedará desactualizada.
   - Si el cambio altera el comportamiento observable (HTTP, códigos de error, campos), dilo:
     entonces no es un refactor y hay que tratarlo como funcionalidad nueva.

3. Refactoriza sin cambiar el comportamiento que la spec no cambió:
   - Respeta la regla de dependencia de architecture.md §2 y las convenciones del proyecto.
   - No mezcles otras mejoras en el mismo cambio; anótalas como propuesta.
   - Si una prueba deja de tener sentido, explícame por qué antes de borrarla o renombrarla.

4. Ejecuta ./mvnw clean test y muéstrame el resultado (número de pruebas y fallos).

5. Propón el commit con prefijo refactor: (o fix: si corrige una desviación respecto a la spec),
   explicando qué spec lo motiva. No hagas commit hasta que lo confirme.
```
