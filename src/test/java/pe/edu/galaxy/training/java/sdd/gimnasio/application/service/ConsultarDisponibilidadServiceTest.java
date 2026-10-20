package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.query.ConsultarDisponibilidadQuery;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.DisponibilidadResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ClaseNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

class ConsultarDisponibilidadServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final Instant TOMORROW = NOW.plus(Duration.ofDays(1));

    private InMemoryClasePersistence clases;
    private InMemoryReservaPersistence reservas;
    private ConsultarDisponibilidadService service;

    @BeforeEach
    void setUp() {
        clases = new InMemoryClasePersistence();
        reservas = new InMemoryReservaPersistence(clases);
        service = new ConsultarDisponibilidadService(clases, reservas);
    }

    @Test
    void US05_CA03_returnsAvailablePlaces() {
        Clase clase = clases.save(Clase.crear("Spinning", "Carla Ruiz", TOMORROW, 60, 20, CLOCK));
        reservas.save(Reserva.crear(SocioId.newId(), clase.getId(), CLOCK));
        reservas.save(Reserva.crear(SocioId.newId(), clase.getId(), CLOCK));
        reservas.save(Reserva.restore(ReservaId.newId(), SocioId.newId(), clase.getId(), NOW,
                EstadoReserva.CANCELADA));

        DisponibilidadResult result = service.consultar(new ConsultarDisponibilidadQuery(clase.getId().value()));

        assertThat(result.claseId()).isEqualTo(clase.getId().value());
        assertThat(result.aforoMaximo()).isEqualTo(20);
        assertThat(result.reservasConfirmadas()).isEqualTo(2);
        assertThat(result.plazasDisponibles()).isEqualTo(18);
    }

    @Test
    void US05_CA03_returnsZeroPlacesWhenClaseIsFull() {
        Clase clase = clases.save(Clase.crear("Spinning", "Carla Ruiz", TOMORROW, 60, 1, CLOCK));
        reservas.save(Reserva.crear(SocioId.newId(), clase.getId(), CLOCK));

        assertThat(service.consultar(new ConsultarDisponibilidadQuery(clase.getId().value())).plazasDisponibles())
                .isZero();
    }

    @Test
    void US05_CA04_rejectsUnknownClase() {
        assertThatThrownBy(() -> service.consultar(new ConsultarDisponibilidadQuery(UUID.randomUUID())))
                .isInstanceOf(ClaseNotFoundException.class);
    }
}
