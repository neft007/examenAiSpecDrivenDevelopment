# Plugin de generación de la API (OpenAPI)

> **Fuentes:** [`pom.xml`](../../pom.xml) (líneas 97–132, plugin; líneas 83–90, copia del contrato),
> [`tech-stack.md`](../architecture/tech-stack.md), [ADR-0006](../adr/0006-spring-boot-4.md) y
> [`gimnasio-openapi.yaml`](../api/gimnasio-openapi.yaml).

## Plugin

`org.openapitools:openapi-generator-maven-plugin`, versión **7.22.0**, con el generador `spring`.
Se ejecuta en la fase `generate-sources` (ejecución `generate-gimnasio-api`), así que cualquier
`./mvnw compile`, `test` o `spring-boot:run` regenera el código antes de compilar.

## Qué genera

Solo interfaces y DTO, en `target/generated-sources/openapi/src/main/java` (no se versiona):

| Paquete | Contenido |
| :--- | :--- |
| `pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated` | Una interfaz por tag: `SociosApi` (`registrarSocio`), `ClasesApi` (`crearClase`) y `ReservasApi` (`reservarPlaza`, `cancelarReserva`, `consultarReservas`, `consultarDisponibilidad`). |
| `…adapter.in.web.generated.model` | DTO de petición y respuesta (`RegistrarSocioRequest`, `SocioResponse`, `CrearClaseRequest`, `ClaseResponse`, `ReservarPlazaRequest`, `ReservaResponse`, `ReservaPageResponse`, `DisponibilidadResponse`, `ErrorResponse`) y los enums `EstadoMembresia`, `EstadoClase` y `EstadoReserva`. |

Cada controlador de `adapter.in.web` implementa la interfaz de su tag (regla 2 de
[`CLAUDE.md`](../../CLAUDE.md)).

## Configuración

Sección `<build><plugins>` del [`pom.xml`](../../pom.xml), líneas 97–132:

| Parámetro | Valor | Efecto |
| :--- | :--- | :--- |
| `inputSpec` | `${project.basedir}/specs/api/gimnasio-openapi.yaml` | Genera a partir del contrato autoritativo. |
| `generatorName` | `spring` | Código para Spring MVC. |
| `apiPackage` / `modelPackage` | `…adapter.in.web.generated` / `…adapter.in.web.generated.model` | El código generado vive en el adaptador web (ADR-0001). |
| `generateSupportingFiles`, `generateApiTests`, `generateModelTests`, `generateApiDocumentation`, `generateModelDocumentation` | `false` | Sin aplicación, pruebas ni documentación generadas. |
| `interfaceOnly` | `true` | Solo interfaces; la implementación son los controladores. |
| `useSpringBoot4` | `true` | Código compatible con Spring Boot 4 (ADR-0006). |
| `useJackson3` | `true` | Código compatible con Jackson 3, el que usa Spring Boot 4. Las anotaciones de los DTO siguen en `com.fasterxml.jackson.annotation`, que Jackson 3 conserva. |
| `useTags` | `true` | Una interfaz por tag del contrato en lugar de una sola. |
| `useBeanValidation` | `true` | Anotaciones `@Valid`, `@Pattern`, `@Size`… en los DTO; sus errores responden `400 VALIDATION_ERROR` (ADR-0004). |
| `openApiNullable` | `false` | Sin `JsonNullable` ni la dependencia `jackson-databind-nullable`. |
| `skipDefaultInterface` | `true` | Métodos abstractos, sin implementación por defecto: el controlador debe implementarlos todos. |
| `documentationProvider` / `annotationLibrary` | `none` | Sin anotaciones de Swagger en el código; la documentación es el propio contrato. |
| `useResponseEntity` | `true` | Los métodos devuelven `ResponseEntity<…>`, para fijar el código HTTP y la cabecera `Location`. |

Además, la sección `<resources>` (líneas 83–90) copia el contrato a `static/` para que Swagger UI lo
sirva en `/gimnasio-openapi.yaml`.

## Cómo se regenera

```bash
./mvnw clean generate-sources
```

## Fuente de verdad

El contrato [`specs/api/gimnasio-openapi.yaml`](../api/gimnasio-openapi.yaml) es la fuente de verdad.
El código generado **no se edita a mano**: para cambiar una interfaz o un DTO se cambia el contrato
(commit `contract:`) y se regenera.

Si cambia la configuración del plugin en el `pom.xml`, se actualiza este documento (y las líneas citadas).
