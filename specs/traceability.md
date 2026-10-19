# Matriz de Trazabilidad

| operationId | Caso de uso (port.in) | Servicio | RF / US | Invariantes | Pruebas mínimas |
| :--- | :--- | :--- | :--- | :--- | :--- |
| registrarSocio | RegistrarSocioUseCase | RegistrarSocioService | RF-01 / US01 | INV-01, INV-02, INV-03, INV-04, INV-05 | US01_CA01 a US01_CA05 |
| crearClase | CrearClaseUseCase | CrearClaseService | RF-02 / US02 | INV-06, INV-07, INV-08 | US02_CA01 a US02_CA04 |
| reservarPlaza | ReservarPlazaUseCase | ReservarPlazaService | RF-03 / US03 | INV-09, INV-10, INV-11, INV-12 | US03_CA01 a US03_CA05 |
| cancelarReserva | CancelarReservaUseCase | CancelarReservaService | RF-04 / US04 | INV-13, INV-14, INV-15 | US04_CA01 a US04_CA03 |
| consultarReservas | ConsultarReservasUseCase | ConsultarReservasService | RF-05 / US05 | — | US05_CA01, US05_CA02 |
| consultarDisponibilidad | ConsultarDisponibilidadUseCase | ConsultarDisponibilidadService | RF-05 / US05 | — | US05_CA03, US05_CA04 |

## Clases de prueba por operación

Dónde estarán las pruebas `USxx_CAyy_...` de cada operación, por capa (`src/test/java/.../gimnasio/`). Las pruebas de dominio son JUnit puro; las de aplicación usan dobles en memoria sin Spring.

| operationId | Dominio (`domain.model`) | Aplicación (`application.service`) | Web (`adapter.in.web`) | Persistencia (`adapter.out.persistence`) |
| :--- | :--- | :--- | :--- | :--- |
| registrarSocio | US01RegistrarSocioTest | RegistrarSocioServiceTest | SocioControllerRegisterTest | SocioPersistenceAdapterTest (US01_CA02, US01_CA04) |
| crearClase | US02CrearClaseTest | CrearClaseServiceTest | ClaseControllerCreateTest | — |
| reservarPlaza | US03ReservarPlazaTest | ReservarPlazaServiceTest | ReservaControllerReservarTest | ReservaPersistenceAdapterTest (US03_CA03, US03_CA04) |
| cancelarReserva | US04CancelarReservaTest | CancelarReservaServiceTest | ReservaControllerCancelarTest | — |
| consultarReservas | — | ConsultarReservasServiceTest | ReservaControllerConsultarTest | — |
| consultarDisponibilidad | — | ConsultarDisponibilidadServiceTest | DisponibilidadControllerTest | ReservaPersistenceAdapterTest (US05_CA03) |

## Pruebas de reglas transversales

| Regla | Clase de prueba |
| :--- | :--- |
| ADR-0004 y ADR-0007: cada fila HTTP + `code` e `INTERNAL_ERROR` sin detalles internos | GlobalExceptionHandlerTest |
| Arranque del contexto completo | GimnasioApplicationTests |