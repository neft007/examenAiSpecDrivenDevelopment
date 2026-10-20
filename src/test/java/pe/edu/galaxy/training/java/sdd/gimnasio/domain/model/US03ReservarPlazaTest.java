package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

/**
 * Dominio de US03: solo la creación de la reserva. CA-02 a CA-05 (INV-09 a INV-12) se prueban en
 * ReservarPlazaServiceTest, porque los valida el servicio.
 */
class US03ReservarPlazaTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void US03_CA01_createsReservaInConfirmadaState() {
        SocioId socioId = SocioId.newId();
        ClaseId claseId = ClaseId.newId();

        Reserva reserva = Reserva.crear(socioId, claseId, CLOCK);

        assertThat(reserva.getId()).isNotNull();
        assertThat(reserva.getSocioId()).isEqualTo(socioId);
        assertThat(reserva.getClaseId()).isEqualTo(claseId);
        assertThat(reserva.getFechaReserva()).isEqualTo(NOW);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
    }

    @Test
    void US03_CA01_generatesUniqueReservaIds() {
        SocioId socioId = SocioId.newId();
        ClaseId claseId = ClaseId.newId();

        assertThat(Reserva.crear(socioId, claseId, CLOCK).getId())
                .isNotEqualTo(Reserva.crear(socioId, claseId, CLOCK).getId());
    }

    @Test
    void restore_rebuildsReservaWithoutApplyingRules() {
        ReservaId id = ReservaId.newId();
        SocioId socioId = SocioId.newId();
        ClaseId claseId = ClaseId.newId();

        Reserva reserva = Reserva.restore(id, socioId, claseId, NOW, EstadoReserva.CANCELADA);

        assertThat(reserva.getId()).isEqualTo(id);
        assertThat(reserva.getSocioId()).isEqualTo(socioId);
        assertThat(reserva.getClaseId()).isEqualTo(claseId);
        assertThat(reserva.getFechaReserva()).isEqualTo(NOW);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }
}
