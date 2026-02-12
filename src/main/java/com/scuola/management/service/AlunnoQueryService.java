package com.scuola.management.service;

import com.scuola.management.domain.*; // for static metamodels
import com.scuola.management.domain.Alunno;
import com.scuola.management.repository.AlunnoRepository;
import com.scuola.management.service.criteria.AlunnoCriteria;
import com.scuola.management.service.dto.AlunnoDTO;
import com.scuola.management.service.mapper.AlunnoMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Alunno} entities in the database.
 * The main input is a {@link AlunnoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AlunnoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AlunnoQueryService extends QueryService<Alunno> {

    private static final Logger LOG = LoggerFactory.getLogger(AlunnoQueryService.class);

    private final AlunnoRepository alunnoRepository;

    private final AlunnoMapper alunnoMapper;

    public AlunnoQueryService(AlunnoRepository alunnoRepository, AlunnoMapper alunnoMapper) {
        this.alunnoRepository = alunnoRepository;
        this.alunnoMapper = alunnoMapper;
    }

    /**
     * Return a {@link Page} of {@link AlunnoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AlunnoDTO> findByCriteria(AlunnoCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Alunno> specification = createSpecification(criteria);
        return alunnoRepository.findAll(specification, page).map(alunnoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AlunnoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Alunno> specification = createSpecification(criteria);
        return alunnoRepository.count(specification);
    }

    /**
     * Function to convert {@link AlunnoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Alunno> createSpecification(AlunnoCriteria criteria) {
        Specification<Alunno> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Alunno_.id),
                buildStringSpecification(criteria.getNome(), Alunno_.nome),
                buildStringSpecification(criteria.getCognome(), Alunno_.cognome),
                buildRangeSpecification(criteria.getDataNascita(), Alunno_.dataNascita),
                buildSpecification(criteria.getCompitoInClasseId(), root ->
                    root.join(Alunno_.compitoInClasses, JoinType.LEFT).get(CompitoInClasse_.id)
                ),
                buildSpecification(criteria.getClasseId(), root -> root.join(Alunno_.classe, JoinType.LEFT).get(Classe_.id))
            );
        }
        return specification;
    }
}
