package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import org.springframework.stereotype.Component;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;

/**
 * Convierte entre el agregado Clase y ClaseJpaEntity.
 */
@Component
public class ClasePersistenceMapper {

    ClaseJpaEntity toEntity(Clase clase) {
        return new ClaseJpaEntity(
                clase.getId().value(),
                clase.getNombre(),
                clase.getInstructor(),
                clase.getFechaInicio(),
                clase.getDuracionMinutos(),
                clase.getAforoMaximo(),
                clase.getEstado());
    }

    Clase toDomain(ClaseJpaEntity entity) {
        return Clase.restore(
                ClaseId.of(entity.getId()),
                entity.getNombre(),
                entity.getInstructor(),
                entity.getFechaInicio(),
                entity.getDuracionMinutos(),
                entity.getAforoMaximo(),
                entity.getEstado());
    }
}
