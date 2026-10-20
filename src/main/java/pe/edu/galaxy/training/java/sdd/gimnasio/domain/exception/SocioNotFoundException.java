package pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception;

import java.util.List;

/**
 * ADR-0004: 404 SOCIO_NOT_FOUND.
 */
public class SocioNotFoundException extends DomainException {

    public SocioNotFoundException(String socioId) {
        super("Socio not found", List.of("socioId: " + socioId + " does not exist"));
    }
}
