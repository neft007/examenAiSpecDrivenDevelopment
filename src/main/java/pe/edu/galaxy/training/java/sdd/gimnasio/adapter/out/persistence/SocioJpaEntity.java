package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import pe.edu.galaxy.training.java.sdd.gimnasio.domain.model.Membresia;

/**
 * Tabla socios. Las restricciones únicas respaldan INV-02 (DNI) e INV-03 (email).
 */
@Entity
@Table(name = "socios", uniqueConstraints = {
        @UniqueConstraint(name = SocioJpaEntity.UK_DNI, columnNames = "dni"),
        @UniqueConstraint(name = SocioJpaEntity.UK_EMAIL, columnNames = "email")
})
public class SocioJpaEntity {

    static final String UK_DNI = "uk_socios_dni";
    static final String UK_EMAIL = "uk_socios_email";

    @Id
    private UUID id;

    @Column(nullable = false, length = 8)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Membresia membresia;

    @Column(name = "fecha_alta", nullable = false)
    private Instant fechaAlta;

    protected SocioJpaEntity() {
    }

    SocioJpaEntity(UUID id, String dni, String nombre, String email, Membresia membresia, Instant fechaAlta) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.membresia = membresia;
        this.fechaAlta = fechaAlta;
    }

    UUID getId() {
        return id;
    }

    String getDni() {
        return dni;
    }

    String getNombre() {
        return nombre;
    }

    String getEmail() {
        return email;
    }

    Membresia getMembresia() {
        return membresia;
    }

    Instant getFechaAlta() {
        return fechaAlta;
    }
}
