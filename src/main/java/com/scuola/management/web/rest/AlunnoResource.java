package com.scuola.management.web.rest;

import com.scuola.management.repository.AlunnoRepository;
import com.scuola.management.service.AlunnoQueryService;
import com.scuola.management.service.AlunnoService;
import com.scuola.management.service.criteria.AlunnoCriteria;
import com.scuola.management.service.dto.AlunnoDTO;
import com.scuola.management.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.scuola.management.domain.Alunno}.
 */
@RestController
@RequestMapping("/api/alunnos")
public class AlunnoResource {

    private static final Logger LOG = LoggerFactory.getLogger(AlunnoResource.class);

    private static final String ENTITY_NAME = "alunno";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AlunnoService alunnoService;

    private final AlunnoRepository alunnoRepository;

    private final AlunnoQueryService alunnoQueryService;

    public AlunnoResource(AlunnoService alunnoService, AlunnoRepository alunnoRepository, AlunnoQueryService alunnoQueryService) {
        this.alunnoService = alunnoService;
        this.alunnoRepository = alunnoRepository;
        this.alunnoQueryService = alunnoQueryService;
    }

    /**
     * {@code POST  /alunnos} : Create a new alunno.
     *
     * @param alunnoDTO the alunnoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new alunnoDTO, or with status {@code 400 (Bad Request)} if the alunno has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AlunnoDTO> createAlunno(@Valid @RequestBody AlunnoDTO alunnoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Alunno : {}", alunnoDTO);
        if (alunnoDTO.getId() != null) {
            throw new BadRequestAlertException("A new alunno cannot already have an ID", ENTITY_NAME, "idexists");
        }
        alunnoDTO = alunnoService.save(alunnoDTO);
        return ResponseEntity.created(new URI("/api/alunnos/" + alunnoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, alunnoDTO.getId().toString()))
            .body(alunnoDTO);
    }

    /**
     * {@code PUT  /alunnos/:id} : Updates an existing alunno.
     *
     * @param id the id of the alunnoDTO to save.
     * @param alunnoDTO the alunnoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alunnoDTO,
     * or with status {@code 400 (Bad Request)} if the alunnoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the alunnoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlunnoDTO> updateAlunno(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AlunnoDTO alunnoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Alunno : {}, {}", id, alunnoDTO);
        if (alunnoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alunnoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alunnoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        alunnoDTO = alunnoService.update(alunnoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, alunnoDTO.getId().toString()))
            .body(alunnoDTO);
    }

    /**
     * {@code PATCH  /alunnos/:id} : Partial updates given fields of an existing alunno, field will ignore if it is null
     *
     * @param id the id of the alunnoDTO to save.
     * @param alunnoDTO the alunnoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated alunnoDTO,
     * or with status {@code 400 (Bad Request)} if the alunnoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the alunnoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the alunnoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AlunnoDTO> partialUpdateAlunno(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AlunnoDTO alunnoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Alunno partially : {}, {}", id, alunnoDTO);
        if (alunnoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, alunnoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!alunnoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AlunnoDTO> result = alunnoService.partialUpdate(alunnoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, alunnoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /alunnos} : get all the alunnos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of alunnos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AlunnoDTO>> getAllAlunnos(
        AlunnoCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Alunnos by criteria: {}", criteria);

        Page<AlunnoDTO> page = alunnoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /alunnos/count} : count all the alunnos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAlunnos(AlunnoCriteria criteria) {
        LOG.debug("REST request to count Alunnos by criteria: {}", criteria);
        return ResponseEntity.ok().body(alunnoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /alunnos/:id} : get the "id" alunno.
     *
     * @param id the id of the alunnoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the alunnoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlunnoDTO> getAlunno(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Alunno : {}", id);
        Optional<AlunnoDTO> alunnoDTO = alunnoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(alunnoDTO);
    }

    /**
     * {@code DELETE  /alunnos/:id} : delete the "id" alunno.
     *
     * @param id the id of the alunnoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlunno(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Alunno : {}", id);
        alunnoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
