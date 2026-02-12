package com.scuola.management.repository;

import com.scuola.management.domain.Alunno;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Alunno entity.
 */
@Repository
public interface AlunnoRepository extends JpaRepository<Alunno, Long>, JpaSpecificationExecutor<Alunno> {
    default Optional<Alunno> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Alunno> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Alunno> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select alunno from Alunno alunno left join fetch alunno.classe", countQuery = "select count(alunno) from Alunno alunno")
    Page<Alunno> findAllWithToOneRelationships(Pageable pageable);

    @Query("select alunno from Alunno alunno left join fetch alunno.classe")
    List<Alunno> findAllWithToOneRelationships();

    @Query("select alunno from Alunno alunno left join fetch alunno.classe where alunno.id =:id")
    Optional<Alunno> findOneWithToOneRelationships(@Param("id") Long id);
}
