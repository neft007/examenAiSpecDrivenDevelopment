# Resumen de Surefire

> **Generado sobre `26df59f`** · **Comando:** `./mvnw clean test` · **Fecha:** 2026-10-20.
> **Fuente:** `target/surefire-reports/TEST-*.xml` (no se versiona; se regenera con el comando).
> **Resultado:** `Tests run: 162, Failures: 0, Errors: 0, Skipped: 0` · `BUILD SUCCESS`.

Las cifras cuentan ejecuciones: cada caso de una prueba parametrizada cuenta como una.

## Por capa

| Capa | Tipo | Clases | Ejecuciones | Fallos | Errores | Omitidas |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: |
| Dominio (`domain.model`) | JUnit puro | 4 | 65 | 0 | 0 | 0 |
| Aplicación (`application.service`) | JUnit puro con dobles en memoria | 6 | 35 | 0 | 0 | 0 |
| Persistencia (`adapter.out.persistence`) | `@DataJpaTest` sobre H2 | 3 | 11 | 0 | 0 | 0 |
| Web (`adapter.in.web`) | `@WebMvcTest` | 7 | 50 | 0 | 0 | 0 |
| Arranque | `@SpringBootTest` | 1 | 1 | 0 | 0 | 0 |
| **Total** | | **21** | **162** | **0** | **0** | **0** |

## Por clase

| Capa | Clase | Ejecuciones | Tiempo (s) |
| :--- | :--- | :---: | ---: |
| Dominio | `US01RegistrarSocioTest` | 24 | 0.031 |
| Dominio | `US02CrearClaseTest` | 32 | 0.074 |
| Dominio | `US03ReservarPlazaTest` | 3 | 0.005 |
| Dominio | `US04CancelarReservaTest` | 6 | 0.082 |
| Aplicación | `RegistrarSocioServiceTest` | 3 | 0.005 |
| Aplicación | `CrearClaseServiceTest` | 10 | 0.014 |
| Aplicación | `ReservarPlazaServiceTest` | 9 | 0.023 |
| Aplicación | `CancelarReservaServiceTest` | 5 | 0.006 |
| Aplicación | `ConsultarReservasServiceTest` | 5 | 0.009 |
| Aplicación | `ConsultarDisponibilidadServiceTest` | 3 | 0.004 |
| Persistencia | `SocioPersistenceAdapterTest` | 3 | 2.573 |
| Persistencia | `ClasePersistenceAdapterTest` | 2 | 0.175 |
| Persistencia | `ReservaPersistenceAdapterTest` | 6 | 0.272 |
| Web | `SocioControllerRegisterTest` | 6 | 0.674 |
| Web | `ClaseControllerCreateTest` | 10 | 0.195 |
| Web | `ReservaControllerReservarTest` | 8 | 0.288 |
| Web | `ReservaControllerCancelarTest` | 4 | 0.028 |
| Web | `ReservaControllerConsultarTest` | 4 | 0.039 |
| Web | `DisponibilidadControllerTest` | 2 | 0.021 |
| Web | `GlobalExceptionHandlerTest` | 16 | 0.174 |
| Arranque | `GimnasioReservasApplicationTests` | 1 | 0.505 |

Los tiempos son los que Surefire registra para cada clase.
