package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 404 RESERVA_NOT_FOUND.
 */
public class ReservaNotFoundException extends DomainException {

    public ReservaNotFoundException(String reservaId) {
        super("Reserva not found", List.of("reservaId: " + reservaId + " does not exist"));
    }
}
