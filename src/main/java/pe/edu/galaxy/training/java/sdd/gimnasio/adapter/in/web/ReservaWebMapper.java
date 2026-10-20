package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web;

import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.stereotype.Component;

import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.DisponibilidadResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.ReservaPageResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.ReservaResponse;
import pe.edu.galaxy.training.java.sdd.gimnasio.adapter.in.web.generated.model.ReservarPlazaRequest;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CancelarReservaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.ReservarPlazaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarDisponibilidadQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarReservasQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.DisponibilidadResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaPageResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;

/**
 * Traduce entre los DTO web del contrato y los modelos de aplicación de reserva (architecture.md §3).
 */
@Component
public class ReservaWebMapper {

    public ReservarPlazaCommand toCommand(UUID claseId, ReservarPlazaRequest request) {
        return new ReservarPlazaCommand(request.getSocioId(), claseId);
    }

    public CancelarReservaCommand toCancelarCommand(UUID reservaId) {
        return new CancelarReservaCommand(reservaId);
    }

    public ConsultarReservasQuery toQuery(UUID socioId, EstadoReserva estado) {
        return new ConsultarReservasQuery(socioId, estado == null ? null
                : pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva.valueOf(estado.name()));
    }

    public ConsultarDisponibilidadQuery toDisponibilidadQuery(UUID claseId) {
        return new ConsultarDisponibilidadQuery(claseId);
    }

    public ReservaResponse toResponse(ReservaResult result) {
        return new ReservaResponse(
                result.id(),
                result.socioId(),
                result.claseId(),
                result.fechaReserva().atOffset(ZoneOffset.UTC),
                EstadoReserva.valueOf(result.estado().name()));
    }

    public ReservaPageResponse toResponse(ReservaPageResult result) {
        return new ReservaPageResponse(result.items().stream().map(this::toResponse).toList(),
                result.totalElements());
    }

    public DisponibilidadResponse toResponse(DisponibilidadResult result) {
        return new DisponibilidadResponse(
                result.claseId(),
                result.aforoMaximo(),
                Math.toIntExact(result.reservasConfirmadas()),
                result.plazasDisponibles());
    }
}
