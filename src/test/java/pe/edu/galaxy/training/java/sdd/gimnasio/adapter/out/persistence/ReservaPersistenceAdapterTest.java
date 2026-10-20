package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoReserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Reserva;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ReservaId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

@DataJpaTest
@Import({ReservaPersistenceAdapter.class, ReservaPersistenceMapper.class,
        ClasePersistenceAdapter.class, ClasePersistenceMapper.class})
class ReservaPersistenceAdapterTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final Instant TOMORROW = NOW.plus(Duration.ofDays(1));

    @Autowired
    private ReservaPersistenceAdapter adapter;

    @Autowired
    private ClasePersistenceAdapter claseAdapter;

    @Autowired
    private EntityManager entityManager;

    private Clase clase;
    private SocioId socioId;

    @BeforeEach
    void setUp() {
        clase = claseAdapter.save(Clase.crear("Spinning", "Carla Ruiz", TOMORROW, 60, 20, CLOCK));
        socioId = SocioId.newId();
    }

    @Test
    void US03_CA01_savesAndRestoresAllFields() {
        Reserva reserva = Reserva.crear(socioId, clase.getId(), CLOCK);
        adapter.save(reserva);
        flushAndClear();

        Reserva restored = adapter.findById(reserva.getId()).orElseThrow();

        assertThat(restored.getId()).isEqualTo(reserva.getId());
        assertThat(restored.getSocioId()).isEqualTo(socioId);
        assertThat(restored.getClaseId()).isEqualTo(clase.getId());
        assertThat(restored.getFechaReserva()).isEqualTo(NOW);
        assertThat(restored.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        assertThat(adapter.findById(ReservaId.newId())).isEmpty();
    }

    @Test
    void US03_CA03_countsReservasByClaseAndEstado() {
        adapter.save(Reserva.crear(SocioId.newId(), clase.getId(), CLOCK));
        adapter.save(Reserva.crear(SocioId.newId(), clase.getId(), CLOCK));
        adapter.save(cancelada(SocioId.newId(), clase));
        Clase otra = claseAdapter.save(Clase.crear("Yoga", "Luis Díaz", TOMORROW, 60, 20, CLOCK));
        adapter.save(Reserva.crear(SocioId.newId(), otra.getId(), CLOCK));
        flushAndClear();

        assertThat(adapter.countByClaseIdAndEstado(clase.getId(), EstadoReserva.CONFIRMADA)).isEqualTo(2);
        assertThat(adapter.countByClaseIdAndEstado(clase.getId(), EstadoReserva.CANCELADA)).isEqualTo(1);
    }

    @Test
    void US05_CA03_countsConfirmadaReservasOfClaseForAvailability() {
        assertThat(adapter.countByClaseIdAndEstado(clase.getId(), EstadoReserva.CONFIRMADA)).isZero();

        Reserva primera = adapter.save(Reserva.crear(SocioId.newId(), clase.getId(), CLOCK));
        adapter.save(Reserva.crear(SocioId.newId(), clase.getId(), CLOCK));
        flushAndClear();
        assertThat(adapter.countByClaseIdAndEstado(clase.getId(), EstadoReserva.CONFIRMADA)).isEqualTo(2);

        // Cancelar libera la plaza: el conteo baja tras guardar la reserva cancelada
        Reserva cancelada = adapter.findById(primera.getId()).orElseThrow();
        cancelada.cancelar(clase.getFechaInicio(), CLOCK);
        adapter.save(cancelada);
        flushAndClear();
        assertThat(adapter.countByClaseIdAndEstado(clase.getId(), EstadoReserva.CONFIRMADA)).isEqualTo(1);
    }

    @Test
    void US03_CA04_detectsActiveReservaOfSocioAtSameStartTime() {
        Clase mismoHorario = claseAdapter.save(Clase.crear("Yoga", "Luis Díaz", TOMORROW, 60, 20, CLOCK));
        adapter.save(Reserva.crear(socioId, clase.getId(), CLOCK));
        flushAndClear();

        assertThat(adapter.existsBySocioIdAndFechaInicio(socioId, mismoHorario.getFechaInicio())).isTrue();
        assertThat(adapter.existsBySocioIdAndFechaInicio(socioId, TOMORROW.plus(Duration.ofHours(1)))).isFalse();
        assertThat(adapter.existsBySocioIdAndFechaInicio(SocioId.newId(), TOMORROW)).isFalse();
    }

    @Test
    void US03_CA04_ignoresCancelledReservasWhenCheckingOverlap() {
        adapter.save(cancelada(socioId, clase));
        flushAndClear();

        assertThat(adapter.existsBySocioIdAndFechaInicio(socioId, TOMORROW)).isFalse();
    }

    @Test
    void findBySocioId_returnsAllReservasOfTheSocio() {
        adapter.save(Reserva.crear(socioId, clase.getId(), CLOCK));
        adapter.save(cancelada(socioId, clase));
        adapter.save(Reserva.crear(SocioId.newId(), clase.getId(), CLOCK));
        flushAndClear();

        assertThat(adapter.findBySocioId(socioId))
                .hasSize(2)
                .allMatch(r -> r.getSocioId().equals(socioId));
    }

    private Reserva cancelada(SocioId socio, Clase claseReservada) {
        return Reserva.restore(ReservaId.newId(), socio, claseReservada.getId(), NOW, EstadoReserva.CANCELADA);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
