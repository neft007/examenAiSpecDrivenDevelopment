package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.RegistrarSocioCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.SocioResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.DniDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.EmailDuplicadoException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Membresia;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.SocioId;

class RegistrarSocioServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    private InMemorySocioPersistence persistence;
    private RegistrarSocioService service;

    @BeforeEach
    void setUp() {
        persistence = new InMemorySocioPersistence();
        service = new RegistrarSocioService(persistence, CLOCK);
    }

    @Test
    void US01_CA01_registersSocioWithActiveMembership() {
        SocioResult result = service.registrar(new RegistrarSocioCommand("12345678", "Ana Pérez", "ana@gym.pe"));

        assertThat(result.id()).isNotNull();
        assertThat(result.dni()).isEqualTo("12345678");
        assertThat(result.nombre()).isEqualTo("Ana Pérez");
        assertThat(result.email()).isEqualTo("ana@gym.pe");
        assertThat(result.membresia()).isEqualTo(Membresia.ACTIVA);
        assertThat(result.fechaAlta()).isEqualTo(NOW);
        assertThat(persistence.findById(SocioId.of(result.id()))).isPresent();
    }

    @Test
    void US01_CA02_rejectsDuplicatedDni() {
        service.registrar(new RegistrarSocioCommand("12345678", "Ana Pérez", "ana@gym.pe"));

        assertThatThrownBy(() -> service.registrar(new RegistrarSocioCommand("12345678", "Luis Díaz", "luis@gym.pe")))
                .isInstanceOf(DniDuplicadoException.class);
        assertThat(persistence.count()).isEqualTo(1);
    }

    @Test
    void US01_CA04_rejectsDuplicatedEmail() {
        service.registrar(new RegistrarSocioCommand("12345678", "Ana Pérez", "ana@gym.pe"));

        assertThatThrownBy(() -> service.registrar(new RegistrarSocioCommand("87654321", "Luis Díaz", "ana@gym.pe")))
                .isInstanceOf(EmailDuplicadoException.class);
        assertThat(persistence.count()).isEqualTo(1);
    }
}
