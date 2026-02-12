package com.scuola.management.web.rest;

import static com.scuola.management.domain.CompitoInClasseAsserts.*;
import static com.scuola.management.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scuola.management.IntegrationTest;
import com.scuola.management.domain.Alunno;
import com.scuola.management.domain.CompitoInClasse;
import com.scuola.management.domain.enumeration.Materia;
import com.scuola.management.repository.CompitoInClasseRepository;
import com.scuola.management.service.dto.CompitoInClasseDTO;
import com.scuola.management.service.mapper.CompitoInClasseMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CompitoInClasseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CompitoInClasseResourceIT {

    private static final LocalDate DEFAULT_DATA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA = LocalDate.ofEpochDay(-1L);

    private static final Materia DEFAULT_MATERIA = Materia.STORIA;
    private static final Materia UPDATED_MATERIA = Materia.ITALIANO;

    private static final Double DEFAULT_RISULTATO = 1D;
    private static final Double UPDATED_RISULTATO = 2D;
    private static final Double SMALLER_RISULTATO = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/compito-in-classes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CompitoInClasseRepository compitoInClasseRepository;

    @Autowired
    private CompitoInClasseMapper compitoInClasseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompitoInClasseMockMvc;

    private CompitoInClasse compitoInClasse;

    private CompitoInClasse insertedCompitoInClasse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompitoInClasse createEntity() {
        return new CompitoInClasse().data(DEFAULT_DATA).materia(DEFAULT_MATERIA).risultato(DEFAULT_RISULTATO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompitoInClasse createUpdatedEntity() {
        return new CompitoInClasse().data(UPDATED_DATA).materia(UPDATED_MATERIA).risultato(UPDATED_RISULTATO);
    }

    @BeforeEach
    void initTest() {
        compitoInClasse = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCompitoInClasse != null) {
            compitoInClasseRepository.delete(insertedCompitoInClasse);
            insertedCompitoInClasse = null;
        }
    }

    @Test
    @Transactional
    void createCompitoInClasse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CompitoInClasse
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);
        var returnedCompitoInClasseDTO = om.readValue(
            restCompitoInClasseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(compitoInClasseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CompitoInClasseDTO.class
        );

        // Validate the CompitoInClasse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCompitoInClasse = compitoInClasseMapper.toEntity(returnedCompitoInClasseDTO);
        assertCompitoInClasseUpdatableFieldsEquals(returnedCompitoInClasse, getPersistedCompitoInClasse(returnedCompitoInClasse));

        insertedCompitoInClasse = returnedCompitoInClasse;
    }

    @Test
    @Transactional
    void createCompitoInClasseWithExistingId() throws Exception {
        // Create the CompitoInClasse with an existing ID
        compitoInClasse.setId(1L);
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompitoInClasseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(compitoInClasseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CompitoInClasse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDataIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        compitoInClasse.setData(null);

        // Create the CompitoInClasse, which fails.
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        restCompitoInClasseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(compitoInClasseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMateriaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        compitoInClasse.setMateria(null);

        // Create the CompitoInClasse, which fails.
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        restCompitoInClasseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(compitoInClasseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRisultatoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        compitoInClasse.setRisultato(null);

        // Create the CompitoInClasse, which fails.
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        restCompitoInClasseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(compitoInClasseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCompitoInClasses() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList
        restCompitoInClasseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compitoInClasse.getId().intValue())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
            .andExpect(jsonPath("$.[*].materia").value(hasItem(DEFAULT_MATERIA.toString())))
            .andExpect(jsonPath("$.[*].risultato").value(hasItem(DEFAULT_RISULTATO)));
    }

    @Test
    @Transactional
    void getCompitoInClasse() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get the compitoInClasse
        restCompitoInClasseMockMvc
            .perform(get(ENTITY_API_URL_ID, compitoInClasse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(compitoInClasse.getId().intValue()))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()))
            .andExpect(jsonPath("$.materia").value(DEFAULT_MATERIA.toString()))
            .andExpect(jsonPath("$.risultato").value(DEFAULT_RISULTATO));
    }

    @Test
    @Transactional
    void getCompitoInClassesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        Long id = compitoInClasse.getId();

        defaultCompitoInClasseFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCompitoInClasseFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCompitoInClasseFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByDataIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where data equals to
        defaultCompitoInClasseFiltering("data.equals=" + DEFAULT_DATA, "data.equals=" + UPDATED_DATA);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByDataIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where data in
        defaultCompitoInClasseFiltering("data.in=" + DEFAULT_DATA + "," + UPDATED_DATA, "data.in=" + UPDATED_DATA);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByDataIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where data is not null
        defaultCompitoInClasseFiltering("data.specified=true", "data.specified=false");
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByDataIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where data is greater than or equal to
        defaultCompitoInClasseFiltering("data.greaterThanOrEqual=" + DEFAULT_DATA, "data.greaterThanOrEqual=" + UPDATED_DATA);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByDataIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where data is less than or equal to
        defaultCompitoInClasseFiltering("data.lessThanOrEqual=" + DEFAULT_DATA, "data.lessThanOrEqual=" + SMALLER_DATA);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByDataIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where data is less than
        defaultCompitoInClasseFiltering("data.lessThan=" + UPDATED_DATA, "data.lessThan=" + DEFAULT_DATA);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByDataIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where data is greater than
        defaultCompitoInClasseFiltering("data.greaterThan=" + SMALLER_DATA, "data.greaterThan=" + DEFAULT_DATA);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByMateriaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where materia equals to
        defaultCompitoInClasseFiltering("materia.equals=" + DEFAULT_MATERIA, "materia.equals=" + UPDATED_MATERIA);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByMateriaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where materia in
        defaultCompitoInClasseFiltering("materia.in=" + DEFAULT_MATERIA + "," + UPDATED_MATERIA, "materia.in=" + UPDATED_MATERIA);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByMateriaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where materia is not null
        defaultCompitoInClasseFiltering("materia.specified=true", "materia.specified=false");
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByRisultatoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where risultato equals to
        defaultCompitoInClasseFiltering("risultato.equals=" + DEFAULT_RISULTATO, "risultato.equals=" + UPDATED_RISULTATO);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByRisultatoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where risultato in
        defaultCompitoInClasseFiltering("risultato.in=" + DEFAULT_RISULTATO + "," + UPDATED_RISULTATO, "risultato.in=" + UPDATED_RISULTATO);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByRisultatoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where risultato is not null
        defaultCompitoInClasseFiltering("risultato.specified=true", "risultato.specified=false");
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByRisultatoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where risultato is greater than or equal to
        defaultCompitoInClasseFiltering(
            "risultato.greaterThanOrEqual=" + DEFAULT_RISULTATO,
            "risultato.greaterThanOrEqual=" + (DEFAULT_RISULTATO + 1)
        );
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByRisultatoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where risultato is less than or equal to
        defaultCompitoInClasseFiltering("risultato.lessThanOrEqual=" + DEFAULT_RISULTATO, "risultato.lessThanOrEqual=" + SMALLER_RISULTATO);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByRisultatoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where risultato is less than
        defaultCompitoInClasseFiltering("risultato.lessThan=" + (DEFAULT_RISULTATO + 1), "risultato.lessThan=" + DEFAULT_RISULTATO);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByRisultatoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        // Get all the compitoInClasseList where risultato is greater than
        defaultCompitoInClasseFiltering("risultato.greaterThan=" + SMALLER_RISULTATO, "risultato.greaterThan=" + DEFAULT_RISULTATO);
    }

    @Test
    @Transactional
    void getAllCompitoInClassesByAlunnoIsEqualToSomething() throws Exception {
        Alunno alunno;
        if (TestUtil.findAll(em, Alunno.class).isEmpty()) {
            compitoInClasseRepository.saveAndFlush(compitoInClasse);
            alunno = AlunnoResourceIT.createEntity(em);
        } else {
            alunno = TestUtil.findAll(em, Alunno.class).get(0);
        }
        em.persist(alunno);
        em.flush();
        compitoInClasse.setAlunno(alunno);
        compitoInClasseRepository.saveAndFlush(compitoInClasse);
        Long alunnoId = alunno.getId();
        // Get all the compitoInClasseList where alunno equals to alunnoId
        defaultCompitoInClasseShouldBeFound("alunnoId.equals=" + alunnoId);

        // Get all the compitoInClasseList where alunno equals to (alunnoId + 1)
        defaultCompitoInClasseShouldNotBeFound("alunnoId.equals=" + (alunnoId + 1));
    }

    private void defaultCompitoInClasseFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCompitoInClasseShouldBeFound(shouldBeFound);
        defaultCompitoInClasseShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCompitoInClasseShouldBeFound(String filter) throws Exception {
        restCompitoInClasseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compitoInClasse.getId().intValue())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
            .andExpect(jsonPath("$.[*].materia").value(hasItem(DEFAULT_MATERIA.toString())))
            .andExpect(jsonPath("$.[*].risultato").value(hasItem(DEFAULT_RISULTATO)));

        // Check, that the count call also returns 1
        restCompitoInClasseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCompitoInClasseShouldNotBeFound(String filter) throws Exception {
        restCompitoInClasseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCompitoInClasseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCompitoInClasse() throws Exception {
        // Get the compitoInClasse
        restCompitoInClasseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCompitoInClasse() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the compitoInClasse
        CompitoInClasse updatedCompitoInClasse = compitoInClasseRepository.findById(compitoInClasse.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCompitoInClasse are not directly saved in db
        em.detach(updatedCompitoInClasse);
        updatedCompitoInClasse.data(UPDATED_DATA).materia(UPDATED_MATERIA).risultato(UPDATED_RISULTATO);
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(updatedCompitoInClasse);

        restCompitoInClasseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, compitoInClasseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(compitoInClasseDTO))
            )
            .andExpect(status().isOk());

        // Validate the CompitoInClasse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCompitoInClasseToMatchAllProperties(updatedCompitoInClasse);
    }

    @Test
    @Transactional
    void putNonExistingCompitoInClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        compitoInClasse.setId(longCount.incrementAndGet());

        // Create the CompitoInClasse
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompitoInClasseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, compitoInClasseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(compitoInClasseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompitoInClasse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompitoInClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        compitoInClasse.setId(longCount.incrementAndGet());

        // Create the CompitoInClasse
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompitoInClasseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(compitoInClasseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompitoInClasse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompitoInClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        compitoInClasse.setId(longCount.incrementAndGet());

        // Create the CompitoInClasse
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompitoInClasseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(compitoInClasseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompitoInClasse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCompitoInClasseWithPatch() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the compitoInClasse using partial update
        CompitoInClasse partialUpdatedCompitoInClasse = new CompitoInClasse();
        partialUpdatedCompitoInClasse.setId(compitoInClasse.getId());

        partialUpdatedCompitoInClasse.materia(UPDATED_MATERIA);

        restCompitoInClasseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompitoInClasse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompitoInClasse))
            )
            .andExpect(status().isOk());

        // Validate the CompitoInClasse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompitoInClasseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCompitoInClasse, compitoInClasse),
            getPersistedCompitoInClasse(compitoInClasse)
        );
    }

    @Test
    @Transactional
    void fullUpdateCompitoInClasseWithPatch() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the compitoInClasse using partial update
        CompitoInClasse partialUpdatedCompitoInClasse = new CompitoInClasse();
        partialUpdatedCompitoInClasse.setId(compitoInClasse.getId());

        partialUpdatedCompitoInClasse.data(UPDATED_DATA).materia(UPDATED_MATERIA).risultato(UPDATED_RISULTATO);

        restCompitoInClasseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompitoInClasse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompitoInClasse))
            )
            .andExpect(status().isOk());

        // Validate the CompitoInClasse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompitoInClasseUpdatableFieldsEquals(
            partialUpdatedCompitoInClasse,
            getPersistedCompitoInClasse(partialUpdatedCompitoInClasse)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCompitoInClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        compitoInClasse.setId(longCount.incrementAndGet());

        // Create the CompitoInClasse
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompitoInClasseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, compitoInClasseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(compitoInClasseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompitoInClasse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompitoInClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        compitoInClasse.setId(longCount.incrementAndGet());

        // Create the CompitoInClasse
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompitoInClasseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(compitoInClasseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompitoInClasse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompitoInClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        compitoInClasse.setId(longCount.incrementAndGet());

        // Create the CompitoInClasse
        CompitoInClasseDTO compitoInClasseDTO = compitoInClasseMapper.toDto(compitoInClasse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompitoInClasseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(compitoInClasseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompitoInClasse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCompitoInClasse() throws Exception {
        // Initialize the database
        insertedCompitoInClasse = compitoInClasseRepository.saveAndFlush(compitoInClasse);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the compitoInClasse
        restCompitoInClasseMockMvc
            .perform(delete(ENTITY_API_URL_ID, compitoInClasse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return compitoInClasseRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected CompitoInClasse getPersistedCompitoInClasse(CompitoInClasse compitoInClasse) {
        return compitoInClasseRepository.findById(compitoInClasse.getId()).orElseThrow();
    }

    protected void assertPersistedCompitoInClasseToMatchAllProperties(CompitoInClasse expectedCompitoInClasse) {
        assertCompitoInClasseAllPropertiesEquals(expectedCompitoInClasse, getPersistedCompitoInClasse(expectedCompitoInClasse));
    }

    protected void assertPersistedCompitoInClasseToMatchUpdatableProperties(CompitoInClasse expectedCompitoInClasse) {
        assertCompitoInClasseAllUpdatablePropertiesEquals(expectedCompitoInClasse, getPersistedCompitoInClasse(expectedCompitoInClasse));
    }
}
