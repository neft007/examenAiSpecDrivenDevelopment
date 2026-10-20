package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CrearClaseCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ClaseResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.CrearClaseUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ClasePersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;

/**
 * Crea una clase (US02). Las reglas (INV-06 a INV-08, INV-16, INV-17) viven en Clase.crear().
 */
@Service
@Transactional
public class CrearClaseService implements CrearClaseUseCase {

    private final ClasePersistencePort persistence;
    private final Clock clock;

    public CrearClaseService(ClasePersistencePort persistence, Clock clock) {
        this.persistence = persistence;
        this.clock = clock;
    }

    @Override
    public ClaseResult crear(CrearClaseCommand command) {
        Clase clase = Clase.crear(command.nombre(), command.instructor(), command.fechaInicio(),
                command.duracionMinutos(), command.aforoMaximo(), clock);
        return ClaseResult.from(persistence.save(clase));
    }
}
