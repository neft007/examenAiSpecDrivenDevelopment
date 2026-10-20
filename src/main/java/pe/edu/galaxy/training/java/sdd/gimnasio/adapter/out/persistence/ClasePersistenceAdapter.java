package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ClasePersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;

/**
 * Implementa ClasePersistencePort con Spring Data JPA.
 */
@Component
public class ClasePersistenceAdapter implements ClasePersistencePort {

    private final SpringDataClaseJpa repository;
    private final ClasePersistenceMapper mapper;

    public ClasePersistenceAdapter(SpringDataClaseJpa repository, ClasePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Clase save(Clase clase) {
        return mapper.toDomain(repository.save(mapper.toEntity(clase)));
    }

    @Override
    public Optional<Clase> findById(ClaseId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }
}
