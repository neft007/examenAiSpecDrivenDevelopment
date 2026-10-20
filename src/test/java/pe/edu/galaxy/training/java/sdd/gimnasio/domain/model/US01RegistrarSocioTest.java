package pe.edu.galaxy.training.java.sdd.gimnasio.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.exception.ValidationException;

class US01RegistrarSocioTest {

    private static final Instant NOW = Instant.parse("2026-10-19T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void US01_CA01_registersSocioWithActiveMembership() {
        Socio socio = Socio.registrar(new Dni("12345678"), "Ana Torres", new Email("ana@gym.pe"), CLOCK);

        assertThat(socio.getId()).isNotNull();
        assertThat(socio.getDni().value()).isEqualTo("12345678");
        assertThat(socio.getNombre()).isEqualTo("Ana Torres");
        assertThat(socio.getEmail().value()).isEqualTo("ana@gym.pe");
        assertThat(socio.getMembresia()).isEqualTo(Membresia.ACTIVA);
        assertThat(socio.getFechaAlta()).isEqualTo(NOW);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "1234567", "123456789", "1234567A", "ABCDEFGH", " 12345678"})
    void US01_CA03_rejectsDniWithInvalidFormat(String value) {
        assertThatThrownBy(() -> new Dni(value))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertThat(((ValidationException) e).getDetails())
                        .singleElement().asString().contains("8 digits"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "ana", "ana@", "@gym.pe", "ana@gym", "ana gym@gym.pe", "ana@@gym.pe"})
    void US01_CA05_rejectsEmailWithInvalidFormat(String value) {
        assertThatThrownBy(() -> new Email(value)).isInstanceOf(ValidationException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "Al", " Ana", "Ana ", "  "})
    void US01_INV05_rejectsInvalidNombre(String nombre) {
        assertThatThrownBy(() -> Socio.registrar(new Dni("12345678"), nombre, new Email("ana@gym.pe"), CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void US01_INV05_rejectsNombreLongerThan100Characters() {
        assertThatThrownBy(() -> Socio.registrar(new Dni("12345678"), "A".repeat(101), new Email("ana@gym.pe"), CLOCK))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void US01_INV05_acceptsNombreAtBoundaries() {
        assertThat(Socio.registrar(new Dni("12345678"), "Ana", new Email("ana@gym.pe"), CLOCK).getNombre())
                .isEqualTo("Ana");
        assertThat(Socio.registrar(new Dni("12345678"), "A".repeat(100), new Email("ana@gym.pe"), CLOCK).getNombre())
                .hasSize(100);
    }
}
