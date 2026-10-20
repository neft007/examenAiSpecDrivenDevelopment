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

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CancelarReservaCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ReservaResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.CancelacionFueraDePlazoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.InvalidStateTransitionException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ReservaNotFoundException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoClase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

class CancelarReservaServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final Instant RESERVADA_EN = NOW.minus(Duration.ofDays(1));

    private InMemoryClasePersistence clases;
    private InMemoryReservaPersistence reservas;
    private CancelarReservaService service;

    @BeforeEach
    void setUp() {
        clases = new InMemoryClasePersistence();
        reservas = new InMemoryReservaPersistence(clases);
        service = new CancelarReservaService(reservas, clases, CLOCK);
    }

    @Test
    void US04_CA01_cancelsReservaWithAtLeastTwoHoursInAdvance() {
        Reserva reserva = reserva(clase(NOW.plus(Duration.ofHours(3))), EstadoReserva.CONFIRMADA);

        ReservaResult result = service.cancelar(new CancelarReservaCommand(reserva.getId().value()));

        assertThat(result.estado()).isEqualTo(EstadoReserva.CANCELADA);
        assertThat(result.fechaReserva()).isEqualTo(RESERVADA_EN);
        assertThat(reservas.findById(reserva.getId()).orElseThrow().getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void US04_CA01_freesThePlaceOfTheClase() {
        Clase clase = clase(NOW.plus(Duration.ofHours(3)));
        Reserva reserva = reserva(clase, EstadoReserva.CONFIRMADA);

        service.cancelar(new CancelarReservaCommand(reserva.getId().value()));

        assertThat(reservas.countByClaseIdAndEstado(clase.getId(), EstadoReserva.CONFIRMADA)).isZero();
    }

    @Test
    void US04_CA02_rejectsCancellationWithLessThanTwoHoursInAdvance() {
        Reserva reserva = reserva(clase(NOW.plus(Duration.ofMinutes(119))), EstadoReserva.CONFIRMADA);

        assertThatThrownBy(() -> service.cancelar(new CancelarReservaCommand(reserva.getId().value())))
                .isInstanceOf(CancelacionFueraDePlazoException.class);
        assertThat(reservas.findById(reserva.getId()).orElseThrow().getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
    }

    @Test
    void US04_CA03_rejectsCancellationOfAlreadyCancelledReserva() {
        Reserva reserva = reserva(clase(NOW.plus(Duration.ofDays(1))), EstadoReserva.CANCELADA);

        assertThatThrownBy(() -> service.cancelar(new CancelarReservaCommand(reserva.getId().value())))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void rejectsUnknownReserva() {
        assertThatThrownBy(() -> service.cancelar(new CancelarReservaCommand(UUID.randomUUID())))
                .isInstanceOf(ReservaNotFoundException.class);
    }

    private Clase clase(Instant fechaInicio) {
        return clases.save(Clase.restore(ClaseId.newId(), "Spinning", "Carla Ruiz", fechaInicio, 60, 20,
                EstadoClase.PROGRAMADA));
    }

    private Reserva reserva(Clase clase, EstadoReserva estado) {
        return reservas.save(Reserva.restore(ReservaId.newId(), SocioId.newId(), clase.getId(), RESERVADA_EN, estado));
    }
}
