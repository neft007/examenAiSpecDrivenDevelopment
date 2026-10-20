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

`mvn test` debe pasar antes de cada commit (regla 9 de `CLAUDE.md`).
