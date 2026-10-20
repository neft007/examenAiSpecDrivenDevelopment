package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.DniDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.EmailDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Dni;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Email;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Membresia;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Socio;

@DataJpaTest
@Import({SocioPersistenceAdapter.class, SocioPersistenceMapper.class})
class SocioPersistenceAdapterTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Autowired
    private SocioPersistenceAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    @Test
    void US01_CA01_savesAndRestoresAllFields() {
        Socio socio = Socio.registrar(new Dni("12345678"), "Ana Pérez", new Email("ana@gym.pe"), CLOCK);
        adapter.save(socio);
        entityManager.clear();

        Socio restored = adapter.findById(socio.getId()).orElseThrow();

        assertThat(restored.getId()).isEqualTo(socio.getId());
        assertThat(restored.getDni()).isEqualTo(new Dni("12345678"));
        assertThat(restored.getNombre()).isEqualTo("Ana Pérez");
        assertThat(restored.getEmail()).isEqualTo(new Email("ana@gym.pe"));
        assertThat(restored.getMembresia()).isEqualTo(Membresia.ACTIVA);
        assertThat(restored.getFechaAlta()).isEqualTo(NOW);
        assertThat(adapter.existsByDni(new Dni("12345678"))).isTrue();
        assertThat(adapter.existsByEmail(new Email("ana@gym.pe"))).isTrue();
    }

    @Test
    void US01_CA02_rejectsDuplicatedDniAtDatabaseLevel() {
        adapter.save(Socio.registrar(new Dni("12345678"), "Ana Pérez", new Email("ana@gym.pe"), CLOCK));
        Socio duplicated = Socio.registrar(new Dni("12345678"), "Luis Díaz", new Email("luis@gym.pe"), CLOCK);

        assertThatThrownBy(() -> adapter.save(duplicated)).isInstanceOf(DniDuplicadoException.class);
    }

    @Test
    void US01_CA04_rejectsDuplicatedEmailAtDatabaseLevel() {
        adapter.save(Socio.registrar(new Dni("12345678"), "Ana Pérez", new Email("ana@gym.pe"), CLOCK));
        Socio duplicated = Socio.registrar(new Dni("87654321"), "Luis Díaz", new Email("ana@gym.pe"), CLOCK);

        assertThatThrownBy(() -> adapter.save(duplicated)).isInstanceOf(EmailDuplicadoException.class);
    }
}
