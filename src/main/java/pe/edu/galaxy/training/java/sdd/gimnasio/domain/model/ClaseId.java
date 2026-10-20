package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador de clase generado por el sistema (ADR-0003).
 */
public record ClaseId(UUID value) {

    public ClaseId {
        Objects.requireNonNull(value, "ClaseId value must not be null");
    }

    public static ClaseId newId() {
        return new ClaseId(UUID.randomUUID());
    }

    public static ClaseId of(UUID value) {
        return new ClaseId(value);
    }
}
