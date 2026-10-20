package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.RegistrarSocioCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.SocioResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.RegistrarSocioUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.SocioPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.DniDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.EmailDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Dni;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Email;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;

/**
 * Registra un socio (US01). Las unicidades INV-02 e INV-03 se comprueban aquí.
 */
@Service
@Transactional
public class RegistrarSocioService implements RegistrarSocioUseCase {

    private final SocioPersistencePort persistence;
    private final Clock clock;

    public RegistrarSocioService(SocioPersistencePort persistence, Clock clock) {
        this.persistence = persistence;
        this.clock = clock;
    }

    @Override
    public SocioResult registrar(RegistrarSocioCommand command) {
        Dni dni = new Dni(command.dni());
        Email email = new Email(command.email());

        // INV-02
        if (persistence.existsByDni(dni)) {
            throw new DniDuplicadoException(dni.value());
        }
        // INV-03
        if (persistence.existsByEmail(email)) {
            throw new EmailDuplicadoException(email.value());
        }

        Socio socio = Socio.registrar(dni, command.nombre(), email, clock);
        return SocioResult.from(persistence.save(socio));
    }
}
