package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador de reserva generado por el sistema (ADR-0003, RF-03 "código único").
 */
public record ReservaId(UUID value) {

    public ReservaId {
        Objects.requireNonNull(value, "ReservaId value must not be null");
    }

    public static ReservaId newId() {
        return new ReservaId(UUID.randomUUID());
    }

    public static ReservaId of(UUID value) {
        return new ReservaId(value);
    }
}
