package com.scuola.management.service;

import com.scuola.management.domain.*; // for static metamodels
import com.scuola.management.domain.CompitoInClasse;
import com.scuola.management.repository.CompitoInClasseRepository;
import com.scuola.management.service.criteria.CompitoInClasseCriteria;
import com.scuola.management.service.dto.CompitoInClasseDTO;
import com.scuola.management.service.mapper.CompitoInClasseMapper;
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
 * Service for executing complex queries for {@link CompitoInClasse} entities in the database.
 * The main input is a {@link CompitoInClasseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CompitoInClasseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CompitoInClasseQueryService extends QueryService<CompitoInClasse> {

    private static final Logger LOG = LoggerFactory.getLogger(CompitoInClasseQueryService.class);

    private final CompitoInClasseRepository compitoInClasseRepository;

    private final CompitoInClasseMapper compitoInClasseMapper;

    public CompitoInClasseQueryService(CompitoInClasseRepository compitoInClasseRepository, CompitoInClasseMapper compitoInClasseMapper) {
        this.compitoInClasseRepository = compitoInClasseRepository;
        this.compitoInClasseMapper = compitoInClasseMapper;
    }

    /**
     * Return a {@link Page} of {@link CompitoInClasseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CompitoInClasseDTO> findByCriteria(CompitoInClasseCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CompitoInClasse> specification = createSpecification(criteria);
        return compitoInClasseRepository.findAll(specification, page).map(compitoInClasseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CompitoInClasseCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CompitoInClasse> specification = createSpecification(criteria);
        return compitoInClasseRepository.count(specification);
    }

    /**
     * Function to convert {@link CompitoInClasseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CompitoInClasse> createSpecification(CompitoInClasseCriteria criteria) {
        Specification<CompitoInClasse> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), CompitoInClasse_.id),
                buildRangeSpecification(criteria.getData(), CompitoInClasse_.data),
                buildSpecification(criteria.getMateria(), CompitoInClasse_.materia),
                buildRangeSpecification(criteria.getRisultato(), CompitoInClasse_.risultato),
                buildSpecification(criteria.getAlunnoId(), root -> root.join(CompitoInClasse_.alunno, JoinType.LEFT).get(Alunno_.id))
            );
        }
        return specification;
    }
}
