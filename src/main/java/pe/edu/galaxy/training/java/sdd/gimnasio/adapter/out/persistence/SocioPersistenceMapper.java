package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import org.springframework.stereotype.Component;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Dni;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Email;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Convierte entre el agregado Socio y SocioJpaEntity.
 */
@Component
public class SocioPersistenceMapper {

    SocioJpaEntity toEntity(Socio socio) {
        return new SocioJpaEntity(
                socio.getId().value(),
                socio.getDni().value(),
                socio.getNombre(),
                socio.getEmail().value(),
                socio.getMembresia(),
                socio.getFechaAlta());
    }

    Socio toDomain(SocioJpaEntity entity) {
        return Socio.restore(
                SocioId.of(entity.getId()),
                new Dni(entity.getDni()),
                entity.getNombre(),
                new Email(entity.getEmail()),
                entity.getMembresia(),
                entity.getFechaAlta());
    }
}
