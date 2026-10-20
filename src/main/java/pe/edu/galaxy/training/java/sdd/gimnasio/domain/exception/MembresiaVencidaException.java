package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 409 MEMBRESIA_VENCIDA (INV-09).
 */
public class MembresiaVencidaException extends DomainException {

    public MembresiaVencidaException(String socioId) {
        super("Membership expired", List.of("socioId: " + socioId + " has an expired membership"));
    }
}
