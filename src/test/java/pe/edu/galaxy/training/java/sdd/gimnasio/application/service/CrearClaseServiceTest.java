package pe.edu.galaxy.training.java.sdd.gimnasio.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.command.CrearClaseCommand;
import pe.edu.galaxy.training.java.sdd.gimnasio.application.port.in.result.ClaseResult;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.ClaseId;
import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoClase;

class CrearClaseServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final Instant TOMORROW = NOW.plus(Duration.ofDays(1));

    private InMemoryClasePersistence persistence;
    private CrearClaseService service;

    @BeforeEach
    void setUp() {
        persistence = new InMemoryClasePersistence();
        service = new CrearClaseService(persistence, CLOCK);
    }

    @Test
    void US02_CA01_createsClaseInProgramadaState() {
        ClaseResult result = service.crear(new CrearClaseCommand("Spinning", "Carla Ruiz", TOMORROW, 60, 20));

        assertThat(result.id()).isNotNull();
        assertThat(result.nombre()).isEqualTo("Spinning");
        assertThat(result.instructor()).isEqualTo("Carla Ruiz");
        assertThat(result.fechaInicio()).isEqualTo(TOMORROW);
        assertThat(result.duracionMinutos()).isEqualTo(60);
        assertThat(result.aforoMaximo()).isEqualTo(20);
        assertThat(result.estado()).isEqualTo(EstadoClase.PROGRAMADA);
        assertThat(persistence.findById(ClaseId.of(result.id()))).isPresent();
    }

    @Test
    void US02_CA02_rejectsStartDateInThePast() {
        assertRejectedAndNotSaved(new CrearClaseCommand("Spinning", "Carla Ruiz", NOW.minusSeconds(1), 60, 20));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 51})
    void US02_CA03_rejectsAforoOutOfRange(int aforo) {
        assertRejectedAndNotSaved(new CrearClaseCommand("Spinning", "Carla Ruiz", TOMORROW, 60, aforo));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 181})
    void US02_CA04_rejectsDuracionOutOfRange(int duracion) {
        assertRejectedAndNotSaved(new CrearClaseCommand("Spinning", "Carla Ruiz", TOMORROW, duracion, 20));
    }

    @ParameterizedTest
    @ValueSource(strings = {" Spinning", "Spinning "})
    void US02_CA05_rejectsNombreWithLeadingOrTrailingWhitespace(String nombre) {
        assertRejectedAndNotSaved(new CrearClaseCommand(nombre, "Carla Ruiz", TOMORROW, 60, 20));
    }

    @ParameterizedTest
    @ValueSource(strings = {" Carla Ruiz", "Carla Ruiz "})
    void US02_CA06_rejectsInstructorWithLeadingOrTrailingWhitespace(String instructor) {
        assertRejectedAndNotSaved(new CrearClaseCommand("Spinning", instructor, TOMORROW, 60, 20));
    }

    private void assertRejectedAndNotSaved(CrearClaseCommand command) {
        assertThatThrownBy(() -> service.crear(command)).isInstanceOf(ValidationException.class);
        assertThat(persistence.count()).isZero();
    }
}
