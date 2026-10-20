package pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out;

import java.util.Optional;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Dni;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Email;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Persistencia de socios (architecture.md §4).
 */
public interface SocioPersistencePort {

    Socio save(Socio socio);

    Optional<Socio> findById(SocioId id);

    boolean existsByDni(Dni dni);

    boolean existsByEmail(Email email);
}
