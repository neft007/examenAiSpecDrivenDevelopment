package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CancelarReservaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.CancelarReservaUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ClasePersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ReservaPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;

/**
 * Cancela una reserva (US04). INV-13 e INV-14 viven en Reserva.cancelar(); el servicio solo aporta
 * la fecha de inicio de la clase, que la reserva no conoce.
 */
@Service
@Transactional
public class CancelarReservaService implements CancelarReservaUseCase {

    private final ReservaPersistencePort reservaPersistence;
    private final ClasePersistencePort clasePersistence;
    private final Clock clock;

    public CancelarReservaService(ReservaPersistencePort reservaPersistence, ClasePersistencePort clasePersistence,
                                  Clock clock) {
        this.reservaPersistence = reservaPersistence;
        this.clasePersistence = clasePersistence;
        this.clock = clock;
    }

    @Override
    public ReservaResult cancelar(CancelarReservaCommand command) {
        Reserva reserva = reservaPersistence.findById(ReservaId.of(command.reservaId()))
                .orElseThrow(() -> new ReservaNotFoundException(command.reservaId().toString()));

        // Una reserva siempre apunta a una clase existente: si falta, es un error de integridad (500)
        Clase clase = clasePersistence.findById(reserva.getClaseId())
                .orElseThrow(() -> new IllegalStateException(
                        "Reserva " + command.reservaId() + " references a missing clase"));

        reserva.cancelar(clase.getFechaInicio(), clock);
        return ReservaResult.from(reservaPersistence.save(reserva));
    }
}
