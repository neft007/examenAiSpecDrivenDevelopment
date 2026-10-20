package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Clase;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoClase;

@DataJpaTest
@Import({ClasePersistenceAdapter.class, ClasePersistenceMapper.class})
class ClasePersistenceAdapterTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final Instant TOMORROW = NOW.plus(Duration.ofDays(1));

    @Autowired
    private ClasePersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    @Test
    void US02_CA01_savesAndRestoresAllFields() {
        Clase clase = Clase.crear("Spinning", "Carla Ruiz", TOMORROW, 60, 20, CLOCK);
        adapter.save(clase);
        entityManager.flush();
        entityManager.clear();

        Clase restored = adapter.findById(clase.getId()).orElseThrow();

        assertThat(restored.getId()).isEqualTo(clase.getId());
        assertThat(restored.getNombre()).isEqualTo("Spinning");
        assertThat(restored.getInstructor()).isEqualTo("Carla Ruiz");
        assertThat(restored.getFechaInicio()).isEqualTo(TOMORROW);
        assertThat(restored.getDuracionMinutos()).isEqualTo(60);
        assertThat(restored.getAforoMaximo()).isEqualTo(20);
        assertThat(restored.getEstado()).isEqualTo(EstadoClase.PROGRAMADA);
    }

    @Test
    void findById_returnsEmptyWhenClaseDoesNotExist() {
        assertThat(adapter.findById(ClaseId.newId())).isEmpty();
    }
}
