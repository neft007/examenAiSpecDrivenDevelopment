package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.EstadoClase;

/**
 * Tabla clases.
 */
@Entity
@Table(name = "clases")
public class ClaseJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String instructor;

    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @Column(name = "duracion_minutos", nullable = false)
    private int duracionMinutos;

    @Column(name = "aforo_maximo", nullable = false)
    private int aforoMaximo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoClase estado;

    protected ClaseJpaEntity() {
    }

    ClaseJpaEntity(UUID id, String nombre, String instructor, Instant fechaInicio, int duracionMinutos,
                   int aforoMaximo, EstadoClase estado) {
        this.id = id;
        this.nombre = nombre;
        this.instructor = instructor;
        this.fechaInicio = fechaInicio;
        this.duracionMinutos = duracionMinutos;
        this.aforoMaximo = aforoMaximo;
        this.estado = estado;
    }

    UUID getId() {
        return id;
    }

    String getNombre() {
        return nombre;
    }

    String getInstructor() {
        return instructor;
    }

    Instant getFechaInicio() {
        return fechaInicio;
    }

    int getDuracionMinutos() {
        return duracionMinutos;
    }

    int getAforoMaximo() {
        return aforoMaximo;
    }

    EstadoClase getEstado() {
        return estado;
    }
}
