package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;

class US02CrearClaseTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final Instant TOMORROW = NOW.plus(Duration.ofDays(1));

    @Test
    void US02_CA01_createsClaseInProgramadaState() {
        Clase clase = Clase.crear("Spinning", "Carla Ruiz", TOMORROW, 60, 20, CLOCK);

        assertThat(clase.getId()).isNotNull();
        assertThat(clase.getNombre()).isEqualTo("Spinning");
        assertThat(clase.getInstructor()).isEqualTo("Carla Ruiz");
        assertThat(clase.getFechaInicio()).isEqualTo(TOMORROW);
        assertThat(clase.getDuracionMinutos()).isEqualTo(60);
        assertThat(clase.getAforoMaximo()).isEqualTo(20);
        assertThat(clase.getEstado()).isEqualTo(EstadoClase.PROGRAMADA);
    }

    @Test
    void US02_CA01_acceptsLimitValues() {
        assertThat(Clase.crear("Yog", "Ana", NOW.plusSeconds(1), 1, 1, CLOCK).getEstado())
                .isEqualTo(EstadoClase.PROGRAMADA);
        assertThat(Clase.crear("A".repeat(100), "B".repeat(100), TOMORROW, 180, 50, CLOCK).getEstado())
                .isEqualTo(EstadoClase.PROGRAMADA);
    }

    @Test
    void US02_CA02_rejectsStartDateInThePast() {
        assertThatThrownBy(() -> Clase.crear("Spinning", "Carla Ruiz", NOW.minusSeconds(1), 60, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void US02_CA02_rejectsStartDateEqualToNow() {
        assertThatThrownBy(() -> Clase.crear("Spinning", "Carla Ruiz", NOW, 60, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void US02_CA02_rejectsMissingStartDate() {
        assertThatThrownBy(() -> Clase.crear("Spinning", "Carla Ruiz", null, 60, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 51, 100})
    void US02_CA03_rejectsAforoOutOfRange(int aforo) {
        assertThatThrownBy(() -> Clase.crear("Spinning", "Carla Ruiz", TOMORROW, 60, aforo, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 181, 240})
    void US02_CA04_rejectsDuracionOutOfRange(int duracion) {
        assertThatThrownBy(() -> Clase.crear("Spinning", "Carla Ruiz", TOMORROW, duracion, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "Yo"})
    void US02_CA05_rejectsInvalidNombre(String nombre) {
        assertThatThrownBy(() -> Clase.crear(nombre, "Carla Ruiz", TOMORROW, 60, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void US02_CA05_rejectsNombreLongerThan100Characters() {
        assertThatThrownBy(() -> Clase.crear("A".repeat(101), "Carla Ruiz", TOMORROW, 60, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {" Spinning", "Spinning ", " Spinning ", "\tSpinning", "   "})
    void US02_CA05_rejectsNombreWithLeadingOrTrailingWhitespace(String nombre) {
        assertThatThrownBy(() -> Clase.crear(nombre, "Carla Ruiz", TOMORROW, 60, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "Al"})
    void US02_CA06_rejectsInvalidInstructor(String instructor) {
        assertThatThrownBy(() -> Clase.crear("Spinning", instructor, TOMORROW, 60, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void US02_CA06_rejectsInstructorLongerThan100Characters() {
        assertThatThrownBy(() -> Clase.crear("Spinning", "B".repeat(101), TOMORROW, 60, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {" Carla Ruiz", "Carla Ruiz ", " Carla Ruiz ", "Carla Ruiz\t", "   "})
    void US02_CA06_rejectsInstructorWithLeadingOrTrailingWhitespace(String instructor) {
        assertThatThrownBy(() -> Clase.crear("Spinning", instructor, TOMORROW, 60, 20, CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void restore_rebuildsClaseWithoutApplyingRules() {
        ClaseId id = ClaseId.newId();
        Instant past = NOW.minus(Duration.ofDays(1));

        Clase clase = Clase.restore(id, "Spinning", "Carla Ruiz", past, 60, 20, EstadoClase.COMPLETADA);

        assertThat(clase.getId()).isEqualTo(id);
        assertThat(clase.getFechaInicio()).isEqualTo(past);
        assertThat(clase.getEstado()).isEqualTo(EstadoClase.COMPLETADA);
    }
}
