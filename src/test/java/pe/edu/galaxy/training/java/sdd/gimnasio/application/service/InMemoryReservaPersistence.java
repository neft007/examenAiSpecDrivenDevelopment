package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ReservaPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Doble en memoria de ReservaPersistencePort para pruebas de aplicación sin Spring. Consulta las clases
 * en InMemoryClasePersistence porque la fecha de inicio pertenece a la clase (INV-12).
 */
class InMemoryReservaPersistence implements ReservaPersistencePort {

    private final Map<ReservaId, Reserva> reservas = new LinkedHashMap<>();
    private final InMemoryClasePersistence clases;

    InMemoryReservaPersistence(InMemoryClasePersistence clases) {
        this.clases = clases;
    }

    @Override
    public Reserva save(Reserva reserva) {
        reservas.put(reserva.getId(), reserva);
        return reserva;
    }

    @Override
    public Optional<Reserva> findById(ReservaId id) {
        return Optional.ofNullable(reservas.get(id));
    }

    @Override
    public long countByClaseIdAndEstado(ClaseId claseId, EstadoReserva estado) {
        return reservas.values().stream()
                .filter(r -> r.getClaseId().equals(claseId) && r.getEstado() == estado)
                .count();
    }

    @Override
    public boolean existsBySocioIdAndFechaInicio(SocioId socioId, Instant fechaInicio) {
        return reservas.values().stream()
                .filter(r -> r.getSocioId().equals(socioId) && r.getEstado() == EstadoReserva.CONFIRMADA)
                .anyMatch(r -> clases.findById(r.getClaseId())
                        .map(c -> c.getFechaInicio().equals(fechaInicio))
                        .orElse(false));
    }

    @Override
    public List<Reserva> findBySocioId(SocioId socioId) {
        return reservas.values().stream().filter(r -> r.getSocioId().equals(socioId)).toList();
    }

    int count() {
        return reservas.size();
    }
}
