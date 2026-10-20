package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import org.springframework.stereotype.Component;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Convierte entre el agregado Reserva y ReservaJpaEntity.
 */
@Component
public class ReservaPersistenceMapper {

    ReservaJpaEntity toEntity(Reserva reserva) {
        return new ReservaJpaEntity(
                reserva.getId().value(),
                reserva.getSocioId().value(),
                reserva.getClaseId().value(),
                reserva.getFechaReserva(),
                reserva.getEstado());
    }

    Reserva toDomain(ReservaJpaEntity entity) {
        return Reserva.restore(
                ReservaId.of(entity.getId()),
                SocioId.of(entity.getSocioId()),
                ClaseId.of(entity.getClaseId()),
                entity.getFechaReserva(),
                entity.getEstado());
    }
}
