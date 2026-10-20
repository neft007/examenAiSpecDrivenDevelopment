# Pruebas

> **Fuentes:** [`specs/traceability.md`](../../specs/traceability.md), [`CLAUDE.md`](../../CLAUDE.md) (regla 7),
> `src/test/java/pe/edu/galaxy/training/java/sdd/gimnasio` y los informes de Surefire de
> `./mvnw clean test` (20 de octubre de 2026: **162 pruebas, 0 fallos**).

## Estrategia

| Capa | Tipo | Herramientas | Qué verifica |
| :--- | :--- | :--- | :--- |
| `domain.model` | Unitaria | JUnit puro, `Clock` fijo | Invariantes de los agregados y Value Objects. |
| `application.service` | Unitaria | JUnit puro con dobles en memoria (`InMemory*Persistence`) | Orquestación y reglas que requieren otros agregados (INV-02, INV-03, INV-09 a INV-12). Sin Spring. |
| `adapter.out.persistence` | Integración | `@DataJpaTest` sobre H2 | Mapeo entidad ↔ dominio, restricciones únicas y consultas (aforo, solapamiento). |
| `adapter.in.web` | Slice web | `@WebMvcTest`, `@MockitoBean` de los casos de uso | HTTP, cuerpo JSON, `Location`, validación del DTO y traducción de errores. |
| Transversal | Slice web | `GlobalExceptionHandlerTest` con un controlador de prueba | Cada fila de ADR-0004 y el 500 genérico de ADR-0007. |
| Arranque | Integración | `@SpringBootTest` (`GimnasioReservasApplicationTests`) | Que el contexto completo se ensambla. |

**Convención de nombres:** `USxx_CAyy_descripcionEnIngles`. Las pruebas que no cubren un CA concreto
(recurso inexistente, `restore`, casos de error transversales) usan un nombre descriptivo sin prefijo.

## Cobertura por criterio de aceptación

✔ = hay al menos una prueba con el prefijo `USxx_CAyy` en esa capa.

| CA | Dominio | Aplicación | Persistencia | Web |
| :--- | :---: | :---: | :---: | :---: |
| US01 CA-01 registra con membresía `ACTIVA` | ✔ | ✔ | ✔ | ✔ |
| US01 CA-02 DNI duplicado | | ✔ | ✔ | ✔ |
| US01 CA-03 DNI con formato inválido | ✔ | | | ✔ |
| US01 CA-04 email duplicado | | ✔ | ✔ | ✔ |
| US01 CA-05 email con formato inválido | ✔ | | | ✔ |
| US01 CA-06 nombre inválido | ✔ | | | ✔ |
| US02 CA-01 crea en `PROGRAMADA` | ✔ | ✔ | ✔ | ✔ |
| US02 CA-02 fecha en el pasado | ✔ | ✔ | | ✔ |
| US02 CA-03 aforo fuera de rango | ✔ | ✔ | | ✔ |
| US02 CA-04 duración fuera de rango | ✔ | ✔ | | ✔ |
| US02 CA-05 nombre inválido | ✔ | ✔ | | ✔ |
| US02 CA-06 instructor inválido | ✔ | ✔ | | ✔ |
| US03 CA-01 reserva `CONFIRMADA` | ✔ | ✔ | ✔ | ✔ |
| US03 CA-02 membresía vencida | | ✔ | | ✔ |
| US03 CA-03 aforo completo | | ✔ | ✔ | ✔ |
| US03 CA-04 reserva solapada | | ✔ | ✔ | ✔ |
| US03 CA-05 clase ya comenzada | | ✔ | | ✔ |
| US04 CA-01 cancela y libera la plaza | ✔ | ✔ | | ✔ |
| US04 CA-02 menos de 2 h | ✔ | ✔ | | ✔ |
| US04 CA-03 ya cancelada | ✔ | ✔ | | ✔ |
| US05 CA-01 solo las reservas del socio | | ✔ | | ✔ |
| US05 CA-02 filtro por estado | | ✔ | | ✔ |
| US05 CA-03 plazas disponibles | | ✔ | ✔ | ✔ |
| US05 CA-04 clase inexistente | | ✔ | | ✔ |

Los 24 criterios de aceptación tienen al menos una prueba (regla 7 de `CLAUDE.md`). Los huecos son
intencionados: cada regla se prueba en la capa donde vive. Por ejemplo, US03 CA-02 a CA-05 no tienen
prueba de dominio porque INV-09 a INV-12 están en el servicio.

## Casos límite cubiertos

- Nombre e instructor con exactamente 3 y 100 caracteres, y con espacios al inicio o al final.
- Fecha de inicio igual a "ahora": se rechaza al crear (INV-06) y al reservar (INV-10).
- Cancelación con exactamente 2 h de antelación (se admite) y de una clase que ya empezó (se rechaza).
- Las reservas canceladas no ocupan plaza ni cuentan para el solapamiento.
- `estado` fuera de catálogo, UUID mal formado, `socioId` ausente y JSON mal formado responden 400.
- Una excepción inesperada responde 500 sin el mensaje interno.

## Clases de prueba

| Capa | Clases (número de ejecuciones) |
| :--- | :--- |
| Dominio | `US01RegistrarSocioTest` (24), `US02CrearClaseTest` (32), `US03ReservarPlazaTest` (3), `US04CancelarReservaTest` (6) |
| Aplicación | `RegistrarSocioServiceTest` (3), `CrearClaseServiceTest` (10), `ReservarPlazaServiceTest` (9), `CancelarReservaServiceTest` (5), `ConsultarReservasServiceTest` (5), `ConsultarDisponibilidadServiceTest` (3) |
| Persistencia | `SocioPersistenceAdapterTest` (3), `ClasePersistenceAdapterTest` (2), `ReservaPersistenceAdapterTest` (6) |
| Web | `SocioControllerRegisterTest` (6), `ClaseControllerCreateTest` (10), `ReservaControllerReservarTest` (8), `ReservaControllerCancelarTest` (4), `ReservaControllerConsultarTest` (4), `DisponibilidadControllerTest` (2), `GlobalExceptionHandlerTest` (16) |
| Arranque | `GimnasioReservasApplicationTests` (1) |

Las cifras cuentan ejecuciones: las pruebas parametrizadas generan una ejecución por caso.
El reparto por operación está en [`traceability.md`](../../specs/traceability.md#clases-de-prueba-por-operación).

## Ejecutar

```bash
./mvnw clean test
```

## Colección de Postman

> **Fuente:** [`postman/gimnasio-reservas-flujo.postman_collection.json`](../../postman/gimnasio-reservas-flujo.postman_collection.json).

La colección **Gimnasio Reservas - Flujo completo** complementa las pruebas automáticas con pruebas
manuales o de humo contra la aplicación en ejecución. No forma parte de `mvn test`.

| Carpeta | Peticiones | Qué cubre |
| :--- | :---: | :--- |
| 1. Flujo principal | 8 | Registrar socio → crear clase → reservar → consultar reservas (con y sin filtro) → disponibilidad → cancelar → la plaza queda libre. |
| 2. a 6. Errores por historia | 27 | Los errores de ADR-0004 de US01 a US05: duplicados, `VALIDATION_ERROR`, `AFORO_COMPLETO`, `RESERVA_SOLAPADA`, `INVALID_STATE_TRANSITION`, `CANCELACION_FUERA_DE_PLAZO` y los tres `*_NOT_FOUND`. |

Cada petición tiene aserciones `pm.test` sobre el código HTTP y el body. Las peticiones se encadenan
con las variables de colección `socioId`, `claseId` y `reservaId`. DNI, email y fechas se generan en
cada ejecución, así que se puede repetir sin reiniciar la aplicación.

**Ejecución** (con la aplicación levantada en `http://localhost:8080`; la colección se ejecuta
completa y en orden):

- **Postman:** importar el archivo y ejecutarlo con el Runner.
- **newman:** `npx newman run postman/gimnasio-reservas-flujo.postman_collection.json`

> **Nota:** `MEMBRESIA_VENCIDA` (US03 CA-02) y `CLASE_YA_COMENZADA` (US03 CA-05) solo se cubren en las
> pruebas automáticas (`ReservarPlazaServiceTest`, `ReservaControllerReservarTest`): en V1 no hay API
> para vencer una membresía ni para crear una clase en el pasado.

`mvn test` debe pasar antes de cada commit (regla 9 de `CLAUDE.md`).

## Validación de specs

> **Fuente:** [`tools/`](../../tools/) (reglas detalladas en [`tools/README.md`](../../tools/README.md)).

Dos scripts de Python 3 (con PyYAML) comprueban la parte del flujo SDD que las pruebas no ven: que las
specs sean coherentes y que el código y las pruebas las sigan.

| Script | Qué valida | Último resultado |
| :--- | :--- | :--- |
| `spec_lint.py` | El contrato OpenAPI contra las specs: YAML y OpenAPI 3.x, enums iguales a `catalogs.md` y `lifecycle.md`, `operationId` y `x-spec-refs`, `ErrorResponse` en las 4xx y `$ref` que resuelven. | OK: 6 operaciones, 0 errores. |
| `spec_sync.py` | El código y las pruebas contra las specs: cada `operationId` implementado en un controlador, cada criterio con una prueba `USxx_CAyy_`, la convención de nombres y las clases de `traceability.md`. | OK: 6 operaciones, 24 criterios, 110 métodos de prueba, 0 errores. |

```bash
python tools/spec_lint.py
python tools/spec_sync.py
```

Las tres formas de verificar el proyecto se complementan:

| Herramienta | Qué demuestra |
| :--- | :--- |
| `./mvnw clean test` | El comportamiento: las reglas y los criterios de aceptación se cumplen. |
| Colección de Postman | La aplicación en ejecución responde según el contrato. |
| `spec_lint.py` y `spec_sync.py` | Specs, contrato, código y pruebas siguen sincronizados. |

Los prompts reutilizables usados durante el desarrollo están documentados en
[`prompts/README.md`](../../prompts/README.md).

El registro de cambios de las specs ([`specs/CHANGELOG.md`](../../specs/CHANGELOG.md)) documenta la
evolución de las specs y su compatibilidad con el contrato.
