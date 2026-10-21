# Stack Tecnológico - Sistema de Reservas para Gimnasio

Fuente única del stack. Las versiones exactas están en `pom.xml`.
Agregar o cambiar una tecnología requiere un ADR.

| Elemento | Tecnología | Uso | Decisión |
| :--- | :--- | :--- | :--- |
| Lenguaje | Java 25 | Todo el código | — |
| Framework | Spring Boot 4.1.x | Web, Validation, Data JPA | ADR-0001, ADR-0006 |
| Base de datos | H2 en memoria (+ consola H2, solo para desarrollo) | Persistencia en `adapter.out.persistence` | ADR-0005 |
| Build | Maven con wrapper (`./mvnw`) | Compilación, generación y pruebas | — |
| Contrato | OpenAPI 3.0.3 | `specs/api/gimnasio-openapi.yaml` | — |
| Pruebas | JUnit 6 (Jupiter) + Spring Boot Test | Dominio sin Spring; adaptadores con Spring | ADR-0006 |
| Generación de API | openapi-generator ≥ 7.22 (`spring`, `interfaceOnly`, `useSpringBoot4`, `useJackson3`) | Las interfaces por tag (`SociosApi`, `ClasesApi`, `ReservasApi`) y DTO web en `adapter.in.web.generated` | ADR-0006 |
| Documentación de la API | springdoc-openapi 3.x (`springdoc-openapi-starter-webmvc-ui`) | Swagger UI que carga el contrato autoritativo (`/gimnasio-openapi.yaml`) | — |

La configuración completa del generador de API (todos los parámetros del `pom.xml`, paquetes generados y
cómo regenerarlo) está documentada en [`resources/plugins.md`](../resources/plugins.md).

## Restricciones

- El paquete `domain` solo usa el JDK (sin Spring, JPA ni librerías externas).
- No se agregan dependencias (ej. Lombok, MapStruct) sin un ADR que las justifique.