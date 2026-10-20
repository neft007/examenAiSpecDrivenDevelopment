package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.CancelarReservaUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.ConsultarDisponibilidadUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.ConsultarReservasUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.ReservarPlazaUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;

/**
 * Base de las pruebas web de ReservaController: ReservasApi agrupa US03, US04 y US05, así que cada
 * prueba necesita los cuatro casos de uso simulados.
 */
abstract class ReservaControllerTestSupport {

    static final UUID RESERVA_ID = UUID.fromString("9c8b7a6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d");
    static final UUID SOCIO_ID = UUID.fromString("3f2b8c1e-4d5a-4b6c-9e7f-1a2b3c4d5e6f");
    static final UUID CLASE_ID = UUID.fromString("7a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d");
    static final Instant FECHA_RESERVA = Instant.parse("2026-10-19T10:00:00Z");

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ReservarPlazaUseCase reservarPlazaUseCase;

    @MockitoBean
    CancelarReservaUseCase cancelarReservaUseCase;

    @MockitoBean
    ConsultarReservasUseCase consultarReservasUseCase;

    @MockitoBean
    ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase;

    static ReservaResult reserva(UUID id, EstadoReserva estado) {
        return new ReservaResult(id, SOCIO_ID, CLASE_ID, FECHA_RESERVA, estado);
    }
}
