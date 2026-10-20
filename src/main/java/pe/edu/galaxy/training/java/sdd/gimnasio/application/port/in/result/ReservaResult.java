package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result;

import java.time.Instant;
import java.util.UUID;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;

/**
 * Salida de los casos de uso de reserva. El dominio nunca sale de la aplicación.
 */
public record ReservaResult(UUID id, UUID socioId, UUID claseId, Instant fechaReserva, EstadoReserva estado) {

    public static ReservaResult from(Reserva reserva) {
        return new ReservaResult(
                reserva.getId().value(),
                reserva.getSocioId().value(),
                reserva.getClaseId().value(),
                reserva.getFechaReserva(),
                reserva.getEstado());
    }
}
