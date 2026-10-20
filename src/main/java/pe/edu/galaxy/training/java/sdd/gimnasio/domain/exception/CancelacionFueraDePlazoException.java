package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 409 CANCELACION_FUERA_DE_PLAZO (INV-14).
 */
public class CancelacionFueraDePlazoException extends DomainException {

    public CancelacionFueraDePlazoException(String reservaId) {
        super("Cancellation out of time",
                List.of("reservaId: " + reservaId + " must be cancelled at least 2 hours before the class starts"));
    }
}
