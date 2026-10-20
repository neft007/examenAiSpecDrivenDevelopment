package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.CancelacionFueraDePlazoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.InvalidStateTransitionException;

class US04CancelarReservaTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final Instant RESERVADA_EN = NOW.minus(Duration.ofDays(1));

    @Test
    void US04_CA01_cancelsReservaWithAtLeastTwoHoursInAdvance() {
        Reserva reserva = confirmada();

        reserva.cancelar(NOW.plus(Duration.ofDays(1)), CLOCK);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        assertThat(reserva.getFechaReserva()).isEqualTo(RESERVADA_EN);
    }

    @Test
    void US04_CA01_cancelsReservaExactlyTwoHoursInAdvance() {
        Reserva reserva = confirmada();

        reserva.cancelar(NOW.plus(Duration.ofHours(2)), CLOCK);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void US04_CA02_rejectsCancellationWithLessThanTwoHoursInAdvance() {
        Reserva reserva = confirmada();

        assertThatThrownBy(() -> reserva.cancelar(NOW.plus(Duration.ofHours(2)).minusSeconds(1), CLOCK))
                .isInstanceOf(CancelacionFueraDePlazoException.class);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
    }

    @Test
    void US04_CA02_rejectsCancellationOfAClassThatAlreadyStarted() {
        Reserva reserva = confirmada();

        assertThatThrownBy(() -> reserva.cancelar(NOW.minus(Duration.ofMinutes(30)), CLOCK))
                .isInstanceOf(CancelacionFueraDePlazoException.class);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
    }

    @Test
    void US04_CA03_rejectsCancellationOfAlreadyCancelledReserva() {
        Reserva reserva = Reserva.restore(ReservaId.newId(), SocioId.newId(), ClaseId.newId(), RESERVADA_EN,
                EstadoReserva.CANCELADA);

        assertThatThrownBy(() -> reserva.cancelar(NOW.plus(Duration.ofDays(1)), CLOCK))
                .isInstanceOf(InvalidStateTransitionException.class);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void US04_CA03_rejectsCancellingTwice() {
        Reserva reserva = confirmada();
        reserva.cancelar(NOW.plus(Duration.ofDays(1)), CLOCK);

        assertThatThrownBy(() -> reserva.cancelar(NOW.plus(Duration.ofDays(1)), CLOCK))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    private static Reserva confirmada() {
        return Reserva.restore(ReservaId.newId(), SocioId.newId(), ClaseId.newId(), RESERVADA_EN,
                EstadoReserva.CONFIRMADA);
    }
}
