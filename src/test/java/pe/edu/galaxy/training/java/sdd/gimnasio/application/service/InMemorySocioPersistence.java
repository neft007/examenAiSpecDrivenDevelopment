package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.SocioPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Dni;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Email;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Doble en memoria de SocioPersistencePort para pruebas de aplicación sin Spring.
 */
class InMemorySocioPersistence implements SocioPersistencePort {

    private final Map<SocioId, Socio> socios = new LinkedHashMap<>();

    @Override
    public Socio save(Socio socio) {
        socios.put(socio.getId(), socio);
        return socio;
    }

    @Override
    public Optional<Socio> findById(SocioId id) {
        return Optional.ofNullable(socios.get(id));
    }

    @Override
    public boolean existsByDni(Dni dni) {
        return socios.values().stream().anyMatch(s -> s.getDni().equals(dni));
    }

    @Override
    public boolean existsByEmail(Email email) {
        return socios.values().stream().anyMatch(s -> s.getEmail().equals(email));
    }

    int count() {
        return socios.size();
    }
}
