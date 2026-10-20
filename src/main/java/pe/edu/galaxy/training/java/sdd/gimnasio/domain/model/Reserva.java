package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.CancelacionFueraDePlazoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.InvalidStateTransitionException;

/**
 * Agregado raíz Reserva (RF-03, RF-04; US03, US04). INV-09 a INV-12 se validan en ReservarPlazaService:
 * requieren conocer el socio, la clase y otras reservas.
 */
public class Reserva {

    private static final Duration ANTELACION_MINIMA_CANCELACION = Duration.ofHours(2);

    private final ReservaId id;
    private final SocioId socioId;
    private final ClaseId claseId;
    private final Instant fechaReserva;
    private EstadoReserva estado;

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

    /**
     * Cancela la reserva (US04). fechaReserva no cambia.
     *
     * @param fechaInicioClase inicio de la clase reservada, necesario para INV-14
     */
    public void cancelar(Instant fechaInicioClase, Clock clock) {
        Objects.requireNonNull(fechaInicioClase, "fechaInicioClase must not be null");
        Objects.requireNonNull(clock, "clock must not be null");
        // INV-13
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new InvalidStateTransitionException(estado.name(), EstadoReserva.CANCELADA.name());
        }
        // INV-14: al menos 2 horas de antelación (exactamente 2 horas se admite)
        if (Duration.between(clock.instant(), fechaInicioClase).compareTo(ANTELACION_MINIMA_CANCELACION) < 0) {
            throw new CancelacionFueraDePlazoException(id.value().toString());
        }
        estado = EstadoReserva.CANCELADA;
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
