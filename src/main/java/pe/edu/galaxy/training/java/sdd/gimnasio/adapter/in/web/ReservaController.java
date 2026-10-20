package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.ReservasApi;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.DisponibilidadResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.ReservaPageResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.ReservaResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.ReservarPlazaRequest;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.CancelarReservaUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.ConsultarDisponibilidadUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.ConsultarReservasUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.ReservarPlazaUseCase;

/**
 * Operaciones del tag Reservas del contrato (US03, US04, US05).
 */
@RestController
public class ReservaController implements ReservasApi {

    private final ReservarPlazaUseCase reservarPlazaUseCase;
    private final CancelarReservaUseCase cancelarReservaUseCase;
    private final ConsultarReservasUseCase consultarReservasUseCase;
    private final ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase;
    private final ReservaWebMapper mapper;

    public ReservaController(ReservarPlazaUseCase reservarPlazaUseCase,
                             CancelarReservaUseCase cancelarReservaUseCase,
                             ConsultarReservasUseCase consultarReservasUseCase,
                             ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase,
                             ReservaWebMapper mapper) {
        this.reservarPlazaUseCase = reservarPlazaUseCase;
        this.cancelarReservaUseCase = cancelarReservaUseCase;
        this.consultarReservasUseCase = consultarReservasUseCase;
        this.consultarDisponibilidadUseCase = consultarDisponibilidadUseCase;
        this.mapper = mapper;
    }

    // El contrato no declara Location en este 201 y no existe GET /reservas/{id}
    @Override
    public ResponseEntity<ReservaResponse> reservarPlaza(UUID claseId, ReservarPlazaRequest reservarPlazaRequest) {
        ReservaResponse response = mapper.toResponse(
                reservarPlazaUseCase.reservar(mapper.toCommand(claseId, reservarPlazaRequest)));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ReservaResponse> cancelarReserva(UUID reservaId) {
        return ResponseEntity.ok(mapper.toResponse(
                cancelarReservaUseCase.cancelar(mapper.toCancelarCommand(reservaId))));
    }

    // estado es opcional en el contrato: null significa sin filtro
    @Override
    public ResponseEntity<ReservaPageResponse> consultarReservas(UUID socioId, EstadoReserva estado) {
        return ResponseEntity.ok(mapper.toResponse(
                consultarReservasUseCase.consultar(mapper.toQuery(socioId, estado))));
    }

    @Override
    public ResponseEntity<DisponibilidadResponse> consultarDisponibilidad(UUID claseId) {
        return ResponseEntity.ok(mapper.toResponse(
                consultarDisponibilidadUseCase.consultar(mapper.toDisponibilidadQuery(claseId))));
    }
}
