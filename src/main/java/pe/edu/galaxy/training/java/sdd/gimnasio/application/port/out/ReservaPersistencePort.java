package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Persistencia de reservas (architecture.md §4).
 */
public interface ReservaPersistencePort {

    Reserva save(Reserva reserva);

    Optional<Reserva> findById(ReservaId id);

    // INV-11
    long countByClaseIdAndEstado(ClaseId claseId, EstadoReserva estado);

    /**
     * INV-12: indica si el socio tiene una reserva activa (CONFIRMADA) en una clase que empieza en fechaInicio.
     */
    boolean existsBySocioIdAndFechaInicio(SocioId socioId, Instant fechaInicio);

    List<Reserva> findBySocioId(SocioId socioId);
}
