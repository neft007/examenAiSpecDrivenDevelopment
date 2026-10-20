package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarDisponibilidadQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.DisponibilidadResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.ConsultarDisponibilidadUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ClasePersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ReservaPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;

/**
 * Consulta las plazas disponibles de una clase (US05).
 */
@Service
@Transactional(readOnly = true)
public class ConsultarDisponibilidadService implements ConsultarDisponibilidadUseCase {

    private final ClasePersistencePort clasePersistence;
    private final ReservaPersistencePort reservaPersistence;

    public ConsultarDisponibilidadService(ClasePersistencePort clasePersistence,
                                          ReservaPersistencePort reservaPersistence) {
        this.clasePersistence = clasePersistence;
        this.reservaPersistence = reservaPersistence;
    }

    @Override
    public DisponibilidadResult consultar(ConsultarDisponibilidadQuery query) {
        ClaseId claseId = ClaseId.of(query.claseId());
        Clase clase = clasePersistence.findById(claseId)
                .orElseThrow(() -> new ClaseNotFoundException(query.claseId().toString()));

        long confirmadas = reservaPersistence.countByClaseIdAndEstado(claseId, EstadoReserva.CONFIRMADA);
        int disponibles = Math.toIntExact(clase.getAforoMaximo() - confirmadas);
        return new DisponibilidadResult(query.claseId(), clase.getAforoMaximo(), confirmadas, disponibles);
    }
}
