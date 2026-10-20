package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 409 INVALID_STATE_TRANSITION (INV-13).
 */
public class InvalidStateTransitionException extends DomainException {

    public InvalidStateTransitionException(String from, String to) {
        super("Invalid state transition", List.of("estado: cannot transition from " + from + " to " + to));
    }
}
