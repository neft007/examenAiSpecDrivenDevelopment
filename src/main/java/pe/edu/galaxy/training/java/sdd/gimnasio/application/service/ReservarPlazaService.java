package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.ReservarPlazaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.ReservarPlazaUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ClasePersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ReservaPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.SocioPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.AforoCompletoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseYaComenzadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.MembresiaVencidaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaSolapadaException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.SocioNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Membresia;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Reserva una plaza (US03). INV-09 a INV-12 se validan aquí, en el orden de architecture.md §6,
 * porque requieren conocer el socio, la clase y otras reservas.
 */
@Service
@Transactional
public class ReservarPlazaService implements ReservarPlazaUseCase {

    private final SocioPersistencePort socioPersistence;
    private final ClasePersistencePort clasePersistence;
    private final ReservaPersistencePort reservaPersistence;
    private final Clock clock;

    public ReservarPlazaService(SocioPersistencePort socioPersistence, ClasePersistencePort clasePersistence,
                                ReservaPersistencePort reservaPersistence, Clock clock) {
        this.socioPersistence = socioPersistence;
        this.clasePersistence = clasePersistence;
        this.reservaPersistence = reservaPersistence;
        this.clock = clock;
    }

    @Override
    public ReservaResult reservar(ReservarPlazaCommand command) {
        SocioId socioId = SocioId.of(command.socioId());
        Socio socio = socioPersistence.findById(socioId)
                .orElseThrow(() -> new SocioNotFoundException(command.socioId().toString()));

        // INV-09
        if (socio.getMembresia() != Membresia.ACTIVA) {
            throw new MembresiaVencidaException(command.socioId().toString());
        }

        ClaseId claseId = ClaseId.of(command.claseId());
        Clase clase = clasePersistence.findById(claseId)
                .orElseThrow(() -> new ClaseNotFoundException(command.claseId().toString()));

        // INV-10: una clase que empieza justo ahora ya comenzó
        if (!clase.getFechaInicio().isAfter(clock.instant())) {
            throw new ClaseYaComenzadaException(command.claseId().toString());
        }

        // INV-11
        if (reservaPersistence.countByClaseIdAndEstado(claseId, EstadoReserva.CONFIRMADA) >= clase.getAforoMaximo()) {
            throw new AforoCompletoException(command.claseId().toString());
        }

        // INV-12
        if (reservaPersistence.existsBySocioIdAndFechaInicio(socioId, clase.getFechaInicio())) {
            throw new ReservaSolapadaException(command.socioId().toString(), clase.getFechaInicio().toString());
        }

        return ReservaResult.from(reservaPersistence.save(Reserva.crear(socioId, claseId, clock)));
    }
}
