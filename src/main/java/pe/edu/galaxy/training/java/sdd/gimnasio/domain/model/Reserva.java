package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * Agregado raíz Reserva (RF-03, US03). INV-09 a INV-12 se validan en ReservarPlazaService:
 * requieren conocer el socio, la clase y otras reservas.
 */
public class Reserva {

    private final ReservaId id;
    private final SocioId socioId;
    private final ClaseId claseId;
    private final Instant fechaReserva;
    private final EstadoReserva estado;

    private Reserva(ReservaId id, SocioId socioId, ClaseId claseId, Instant fechaReserva, EstadoReserva estado) {
        this.id = id;
        this.socioId = socioId;
        this.claseId = claseId;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }

    /**
     * Crea una reserva nueva en estado CONFIRMADA (US03 CA-01).
     */
    public static Reserva crear(SocioId socioId, ClaseId claseId, Clock clock) {
        Objects.requireNonNull(socioId, "socioId must not be null");
        Objects.requireNonNull(claseId, "claseId must not be null");
        Objects.requireNonNull(clock, "clock must not be null");
        return new Reserva(ReservaId.newId(), socioId, claseId, Instant.now(clock), EstadoReserva.CONFIRMADA);
    }

    /**
     * Reconstruye una reserva ya guardada, sin aplicar reglas. Solo para adaptadores de persistencia.
     */
    public static Reserva restore(ReservaId id, SocioId socioId, ClaseId claseId, Instant fechaReserva,
                                  EstadoReserva estado) {
        return new Reserva(id, socioId, claseId, fechaReserva, estado);
    }

    public ReservaId getId() {
        return id;
    }

    public SocioId getSocioId() {
        return socioId;
    }

    public ClaseId getClaseId() {
        return claseId;
    }

    public Instant getFechaReserva() {
        return fechaReserva;
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}
