# Matriz de Trazabilidad

| operationId | Caso de uso (port.in) | Servicio | RF / US | Invariantes | Pruebas mínimas |
| :--- | :--- | :--- | :--- | :--- | :--- |
| registrarSocio | RegistrarSocioUseCase | RegistrarSocioService | RF-01 / US01 | INV-01, INV-02, INV-03, INV-04, INV-05 | US01_CA01 a US01_CA06 |
| crearClase | CrearClaseUseCase | CrearClaseService | RF-02 / US02 | INV-06, INV-07, INV-08, INV-16, INV-17 | US02_CA01 a US02_CA06 |
| reservarPlaza | ReservarPlazaUseCase | ReservarPlazaService | RF-03 / US03 | INV-09, INV-10, INV-11, INV-12 | US03_CA01 a US03_CA05. Nota: US03_CA02 se prueba con un socio en estado VENCIDA creado con restore (no hay flujo de API para cambiarlo en V1). |
| cancelarReserva | CancelarReservaUseCase | CancelarReservaService | RF-04 / US04 | INV-13, INV-14, INV-15 | US04_CA01 a US04_CA03 |
| consultarReservas | ConsultarReservasUseCase | ConsultarReservasService | RF-05 / US05 | — | US05_CA01, US05_CA02 |
| consultarDisponibilidad | ConsultarDisponibilidadUseCase | ConsultarDisponibilidadService | RF-05 / US05 | — | US05_CA03, US05_CA04 |

Cada `operationId` es un método de la interfaz generada de su tag: `SociosApi` (registrarSocio), `ClasesApi` (crearClase) y `ReservasApi` (reservarPlaza, cancelarReserva, consultarReservas, consultarDisponibilidad), que implementa el controlador correspondiente. La configuración del generador está documentada en [`resources/plugins.md`](resources/plugins.md).

## Clases de prueba por operación

Dónde están las pruebas `USxx_CAyy_...` de cada operación, por capa (`src/test/java/.../gimnasio/`). Las pruebas de dominio son JUnit puro; las de aplicación usan dobles en memoria sin Spring.

| operationId | Dominio (`domain.model`) | Aplicación (`application.service`) | Web (`adapter.in.web`) | Persistencia (`adapter.out.persistence`) |
| :--- | :--- | :--- | :--- | :--- |
| registrarSocio | US01RegistrarSocioTest | RegistrarSocioServiceTest | SocioControllerRegisterTest | SocioPersistenceAdapterTest (US01_CA01, US01_CA02, US01_CA04) |
| crearClase | US02CrearClaseTest | CrearClaseServiceTest | ClaseControllerCreateTest | ClasePersistenceAdapterTest (US02_CA01) |
| reservarPlaza | US03ReservarPlazaTest | ReservarPlazaServiceTest | ReservaControllerReservarTest | ReservaPersistenceAdapterTest (US03_CA01, US03_CA03, US03_CA04) |
| cancelarReserva | US04CancelarReservaTest | CancelarReservaServiceTest | ReservaControllerCancelarTest | — |
| consultarReservas | — | ConsultarReservasServiceTest | ReservaControllerConsultarTest | ReservaPersistenceAdapterTest (`findBySocioId`) |
| consultarDisponibilidad | — | ConsultarDisponibilidadServiceTest | DisponibilidadControllerTest | ReservaPersistenceAdapterTest (US05_CA03) |

## Pruebas de reglas transversales

| Regla | Clase de prueba |
| :--- | :--- |
| ADR-0004 y ADR-0007: cada fila HTTP + `code` e `INTERNAL_ERROR` sin detalles internos | GlobalExceptionHandlerTest |
| Arranque del contexto completo | GimnasioReservasApplicationTests |