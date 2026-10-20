package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarReservasQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaPageResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.usecase.ConsultarReservasUseCase;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.ReservaPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.out.SocioPersistencePort;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.SocioNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

/**
 * Consulta las reservas de un socio, con filtro opcional por estado (US05).
 */
@Service
@Transactional(readOnly = true)
public class ConsultarReservasService implements ConsultarReservasUseCase {

    private final SocioPersistencePort socioPersistence;
    private final ReservaPersistencePort reservaPersistence;

    public ConsultarReservasService(SocioPersistencePort socioPersistence,
                                    ReservaPersistencePort reservaPersistence) {
        this.socioPersistence = socioPersistence;
        this.reservaPersistence = reservaPersistence;
    }

    @Override
    public ReservaPageResult consultar(ConsultarReservasQuery query) {
        SocioId socioId = SocioId.of(query.socioId());
        if (socioPersistence.findById(socioId).isEmpty()) {
            throw new SocioNotFoundException(query.socioId().toString());
        }

        List<ReservaResult> items = reservaPersistence.findBySocioId(socioId).stream()
                .filter(reserva -> query.estado() == null || reserva.getEstado() == query.estado())
                .map(ReservaResult::from)
                .toList();
        return new ReservaPageResult(items, items.size());
    }
}
