package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 409 AFORO_COMPLETO (INV-11).
 */
public class AforoCompletoException extends DomainException {

    public AforoCompletoException(String claseId) {
        super("Class is full", List.of("claseId: " + claseId + " has no available places"));
    }
}
