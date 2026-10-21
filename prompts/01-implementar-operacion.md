# Prompt 01 - Implementar una operación completa

## Cuándo usarlo

Cuando el dominio de la historia ya existe (prompt 00) y hay que exponer una operación del contrato de
punta a punta: puertos, servicio, persistencia y web. Puede usarse capa por capa, repitiéndolo con
"solo la capa X".

## Variables

| Variable | Qué poner | Ejemplo |
| :--- | :--- | :--- |
| `{{OPERACION}}` | `operationId` del contrato. | `reservarPlaza` |
| `{{HISTORIA}}` | Historia que la cubre. | `US03` |

## Prompt

```text
Implementa la operación {{OPERACION}} de {{HISTORIA}}.

1. Lee antes de escribir código:
   - specs/api/gimnasio-openapi.yaml: ruta, request, respuestas y códigos de {{OPERACION}}.
   - specs/traceability.md: caso de uso, servicio, invariantes y pruebas mínimas de {{OPERACION}}.
   - specs/architecture/architecture.md: capas, puertos (§4), modelos por frontera (§3) y,
     si existe, el diagrama de secuencia de la operación.
   - specs/user-stories/ ({{HISTORIA}}) y specs/adr/0004-codigos-de-error.md.

2. Si la spec no alcanza o se contradice (por ejemplo, el contrato no declara una cabecera o un
   código de error que la historia necesita), DETENTE y propónme opciones. No inventes campos,
   rutas, cabeceras ni códigos.

3. application.port.in:
   - {{OPERACION}}UseCase con la firma de architecture.md §4.
   - Command o Query y Result como record. Usan los enums del dominio, nunca String ni los enums
     generados. El Result se construye con un from(entidad); el dominio nunca sale de application.

4. application.port.out: solo las operaciones del puerto que lista architecture.md §4.

5. application.service: un {{OPERACION}}Service.
   - Orquesta; las reglas que dependen de otros agregados se comprueban en el orden de la spec,
     cada una con su comentario // INV-xx.
   - @Transactional (readOnly = true si es una consulta). El tiempo viene del Clock inyectado.

6. adapter.out.persistence: *JpaEntity, *PersistenceMapper, repositorio Spring Data y
   *PersistenceAdapter que implementa el puerto. Las restricciones de BD que respaldan una
   invariante se traducen a la excepción de dominio correspondiente.

7. adapter.in.web:
   - El controlador implementa la interfaz generada de su tag (SociosApi, ClasesApi, ReservasApi);
     no edites el código generado. La configuración del generador OpenAPI está documentada en
     specs/resources/plugins.md.
   - El *WebMapper es el único sitio que convierte DTO <-> Command/Result, enums del contrato <->
     enums del dominio (valueOf(name())) e Instant <-> OffsetDateTime en UTC.
   - Devuelve exactamente el HTTP y las cabeceras del contrato.
   - Cada excepción nueva se mapea en GlobalExceptionHandler a su fila de ADR-0004.

8. Pruebas, al menos una por criterio de aceptación, con nombres USxx_CAyy_descripcionEnIngles:
   - Servicio: JUnit puro con dobles en memoria del puerto de salida (sin Spring).
   - Persistencia: @DataJpaTest sobre H2 (consultas y restricciones).
   - Web: @WebMvcTest con el caso de uso en @MockitoBean (HTTP, body, cabeceras y errores).
   - Si añades una excepción, amplía GlobalExceptionHandlerTest.

9. Ejecuta ./mvnw clean test. Propón commits que pasen mvn test cada uno por separado
   (persistencia antes que el servicio que la necesita, para que el contexto arranque) con
   prefijos feat(persistence):, feat(application):, feat(web):. No hagas commit hasta que lo confirme.

Al terminar, actualiza la fila de {{OPERACION}} en specs/traceability.md si cambió alguna clase de prueba.
```
