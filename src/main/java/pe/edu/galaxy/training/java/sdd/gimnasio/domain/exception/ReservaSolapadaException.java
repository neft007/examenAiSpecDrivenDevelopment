package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 409 RESERVA_SOLAPADA (INV-12).
 */
public class ReservaSolapadaException extends DomainException {

    public ReservaSolapadaException(String socioId, String fechaInicio) {
        super("Overlapping reservation",
                List.of("socioId: " + socioId + " already has a reservation at " + fechaInicio));
    }
}
