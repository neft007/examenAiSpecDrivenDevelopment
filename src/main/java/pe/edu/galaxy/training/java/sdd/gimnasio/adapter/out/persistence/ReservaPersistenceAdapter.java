package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ReservaPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Implementa ReservaPersistencePort con Spring Data JPA.
 */
@Component
public class ReservaPersistenceAdapter implements ReservaPersistencePort {

    private final SpringDataReservaJpa repository;
    private final ReservaPersistenceMapper mapper;

    public ReservaPersistenceAdapter(SpringDataReservaJpa repository, ReservaPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Reserva save(Reserva reserva) {
        return mapper.toDomain(repository.save(mapper.toEntity(reserva)));
    }

    @Override
    public Optional<Reserva> findById(ReservaId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public long countByClaseIdAndEstado(ClaseId claseId, EstadoReserva estado) {
        return repository.countByClaseIdAndEstado(claseId.value(), estado);
    }

    // INV-12 habla de reservas activas: solo cuentan las CONFIRMADA
    @Override
    public boolean existsBySocioIdAndFechaInicio(SocioId socioId, Instant fechaInicio) {
        return repository.existsBySocioIdAndEstadoAndClaseFechaInicio(socioId.value(), EstadoReserva.CONFIRMADA,
                fechaInicio);
    }

    @Override
    public List<Reserva> findBySocioId(SocioId socioId) {
        return repository.findBySocioId(socioId.value()).stream().map(mapper::toDomain).toList();
    }
}
