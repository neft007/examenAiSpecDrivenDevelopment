package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data de socios. Solo lo usa SocioPersistenceAdapter.
 */
interface SpringDataSocioJpa extends JpaRepository<SocioJpaEntity, UUID> {

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);
}
