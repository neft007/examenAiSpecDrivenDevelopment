package pe.edu.galaxy.training.java.sdd.gimnasio.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data de clases. Solo lo usa ClasePersistenceAdapter.
 */
interface SpringDataClaseJpa extends JpaRepository<ClaseJpaEntity, UUID> {
}
