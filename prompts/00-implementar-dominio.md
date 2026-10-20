# Prompt 00 - Implementar la capa de dominio de una historia

## Cuándo usarlo

Al empezar una historia de usuario nueva, antes de tocar `application` o los adaptadores. El resultado
es un dominio en Java puro, probado con JUnit sin Spring.

## Variables

| Variable | Qué poner | Ejemplo |
| :--- | :--- | :--- |
| `{{HISTORIA}}` | Identificador y archivo de la historia. | `US02` (`specs/user-stories/US02-crear-clase.md`) |
| `{{INVARIANTES}}` | Invariantes de `domain-model.md` §3 que se garantizan en el dominio. | `INV-06, INV-07, INV-08, INV-16, INV-17` |

## Prompt

```text
Implementa la capa de dominio de {{HISTORIA}}.

1. Lee antes de escribir código:
   - specs/user-stories/ (la historia {{HISTORIA}} y sus criterios de aceptación).
   - specs/requirements/functional.md (el RF de la historia).
   - specs/domain/lifecycle.md y specs/domain/catalogs.md (estados y catálogos).
   - specs/architecture/domain-model.md (glosario, diagrama de clases, invariantes {{INVARIANTES}}
     y decisiones de modelado).
   - specs/adr/0004-codigos-de-error.md (una excepción por fila).

2. Si la spec no alcanza, DETENTE y dime qué falta antes de implementar: un campo, una regla,
   un límite (¿inclusivo o exclusivo?), un estado o un código de error que no esté definido.
   Si la spec y mis instrucciones se contradicen, sigue la spec y explícame por qué.

3. Crea en domain.model y domain.exception, solo lo que pida el diagrama de clases:
   - El agregado raíz, con una fábrica estática que aplica las invariantes ({{INVARIANTES}})
     y un restore(...) que reconstruye sin aplicar reglas (solo para persistencia).
   - Los Value Objects (record) que validan su formato al construirse.
   - Los enums, con los mismos valores y el mismo orden que catalogs.md.
   - Las excepciones que falten, como subclases de DomainException(message, details).
   - Los métodos que fijan fechas reciben un java.time.Clock.
   - No añadas métodos que no estén en el diagrama ni respaldados por una historia.

4. Reglas del dominio:
   - Java puro: nada de Spring, JPA ni librerías externas; sin puertos.
   - Marca cada regla con un comentario // INV-xx.
   - Las reglas que necesitan conocer otros agregados (unicidad, aforo, solapamiento) NO van
     aquí: van en el servicio de aplicación.

5. Pruebas en src/test/java/.../domain/model/USxxNombreHistoriaTest.java:
   - Al menos una por criterio de aceptación que dependa del dominio, con el nombre
     USxx_CAyy_descripcionEnIngles.
   - Clock fijo; incluye los valores límite (mínimo, máximo, igual a "ahora").
   - JUnit puro, sin Spring.

6. Ejecuta ./mvnw clean test y muéstrame el resultado. No hagas commit hasta que te lo pida;
   cuando lo pida, usa el prefijo feat(domain): o test: según corresponda.

Al terminar, resume qué invariante quedó en cada clase y qué criterios quedan para otras capas.
```
