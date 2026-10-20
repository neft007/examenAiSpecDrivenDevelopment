package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador de socio generado por el sistema (ADR-0003).
 */
public record SocioId(UUID value) {

    public SocioId {
        Objects.requireNonNull(value, "SocioId value must not be null");
    }

    public static SocioId newId() {
        return new SocioId(UUID.randomUUID());
    }

    public static SocioId of(UUID value) {
        return new SocioId(value);
    }
}
