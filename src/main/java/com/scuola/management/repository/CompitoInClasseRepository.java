package com.scuola.management.repository;

import com.scuola.management.domain.CompitoInClasse;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CompitoInClasse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompitoInClasseRepository extends JpaRepository<CompitoInClasse, Long>, JpaSpecificationExecutor<CompitoInClasse> {}
