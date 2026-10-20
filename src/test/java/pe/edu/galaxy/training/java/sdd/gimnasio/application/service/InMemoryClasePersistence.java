package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ClasePersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;

/**
 * Doble en memoria de ClasePersistencePort para pruebas de aplicación sin Spring.
 */
class InMemoryClasePersistence implements ClasePersistencePort {

    private final Map<ClaseId, Clase> clases = new LinkedHashMap<>();

    @Override
    public Clase save(Clase clase) {
        clases.put(clase.getId(), clase);
        return clase;
    }

    @Override
    public Optional<Clase> findById(ClaseId id) {
        return Optional.ofNullable(clases.get(id));
    }

    int count() {
        return clases.size();
    }
}
