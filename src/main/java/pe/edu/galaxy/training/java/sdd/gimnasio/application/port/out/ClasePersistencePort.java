package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out;

import java.util.Optional;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;

/**
 * Persistencia de clases (architecture.md §4).
 */
public interface ClasePersistencePort {

    Clase save(Clase clase);

    Optional<Clase> findById(ClaseId id);
}
