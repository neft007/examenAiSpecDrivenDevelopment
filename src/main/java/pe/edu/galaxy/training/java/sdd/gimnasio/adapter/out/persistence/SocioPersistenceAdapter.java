package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import java.util.Locale;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.SocioPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.DniDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.EmailDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Dni;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Email;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Implementa SocioPersistencePort con Spring Data JPA.
 */
@Component
public class SocioPersistenceAdapter implements SocioPersistencePort {

    private final SpringDataSocioJpa repository;
    private final SocioPersistenceMapper mapper;

    public SocioPersistenceAdapter(SpringDataSocioJpa repository, SocioPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Socio save(Socio socio) {
        try {
            return mapper.toDomain(repository.saveAndFlush(mapper.toEntity(socio)));
        } catch (DataIntegrityViolationException ex) {
            // Respaldo en BD de INV-02 e INV-03 si otra transacción se adelantó a la comprobación del servicio
            String cause = String.valueOf(ex.getMostSpecificCause().getMessage()).toLowerCase(Locale.ROOT);
            if (cause.contains(SocioJpaEntity.UK_DNI)) {
                throw new DniDuplicadoException(socio.getDni().value());
            }
            if (cause.contains(SocioJpaEntity.UK_EMAIL)) {
                throw new EmailDuplicadoException(socio.getEmail().value());
            }
            throw ex;
        }
    }

    @Override
    public Optional<Socio> findById(SocioId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByDni(Dni dni) {
        return repository.existsByDni(dni.value());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }
}
