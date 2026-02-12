package com.scuola.management.web.rest;

import static com.scuola.management.domain.AlunnoAsserts.*;
import static com.scuola.management.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scuola.management.IntegrationTest;
import com.scuola.management.domain.Alunno;
import com.scuola.management.domain.Classe;
import com.scuola.management.repository.AlunnoRepository;
import com.scuola.management.service.AlunnoService;
import com.scuola.management.service.dto.AlunnoDTO;
import com.scuola.management.service.mapper.AlunnoMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AlunnoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AlunnoResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_COGNOME = "AAAAAAAAAA";
    private static final String UPDATED_COGNOME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_NASCITA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_NASCITA = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_NASCITA = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/alunnos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlunnoRepository alunnoRepository;

    @Mock
    private AlunnoRepository alunnoRepositoryMock;

    @Autowired
    private AlunnoMapper alunnoMapper;

    @Mock
    private AlunnoService alunnoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlunnoMockMvc;

    private Alunno alunno;

    private Alunno insertedAlunno;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alunno createEntity(EntityManager em) {
        Alunno alunno = new Alunno().nome(DEFAULT_NOME).cognome(DEFAULT_COGNOME).dataNascita(DEFAULT_DATA_NASCITA);
        // Add required entity
        Classe classe;
        if (TestUtil.findAll(em, Classe.class).isEmpty()) {
            classe = ClasseResourceIT.createEntity();
            em.persist(classe);
            em.flush();
        } else {
            classe = TestUtil.findAll(em, Classe.class).get(0);
        }
        alunno.setClasse(classe);
        return alunno;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alunno createUpdatedEntity(EntityManager em) {
        Alunno updatedAlunno = new Alunno().nome(UPDATED_NOME).cognome(UPDATED_COGNOME).dataNascita(UPDATED_DATA_NASCITA);
        // Add required entity
        Classe classe;
        if (TestUtil.findAll(em, Classe.class).isEmpty()) {
            classe = ClasseResourceIT.createUpdatedEntity();
            em.persist(classe);
            em.flush();
        } else {
            classe = TestUtil.findAll(em, Classe.class).get(0);
        }
        updatedAlunno.setClasse(classe);
        return updatedAlunno;
    }

    @BeforeEach
    void initTest() {
        alunno = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAlunno != null) {
            alunnoRepository.delete(insertedAlunno);
            insertedAlunno = null;
        }
    }

    @Test
    @Transactional
    void createAlunno() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Alunno
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);
        var returnedAlunnoDTO = om.readValue(
            restAlunnoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alunnoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AlunnoDTO.class
        );

        // Validate the Alunno in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAlunno = alunnoMapper.toEntity(returnedAlunnoDTO);
        assertAlunnoUpdatableFieldsEquals(returnedAlunno, getPersistedAlunno(returnedAlunno));

        insertedAlunno = returnedAlunno;
    }

    @Test
    @Transactional
    void createAlunnoWithExistingId() throws Exception {
        // Create the Alunno with an existing ID
        alunno.setId(1L);
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlunnoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alunnoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alunno in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alunno.setNome(null);

        // Create the Alunno, which fails.
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        restAlunnoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alunnoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCognomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alunno.setCognome(null);

        // Create the Alunno, which fails.
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        restAlunnoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alunnoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDataNascitaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alunno.setDataNascita(null);

        // Create the Alunno, which fails.
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        restAlunnoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alunnoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAlunnos() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList
        restAlunnoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alunno.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].cognome").value(hasItem(DEFAULT_COGNOME)))
            .andExpect(jsonPath("$.[*].dataNascita").value(hasItem(DEFAULT_DATA_NASCITA.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlunnosWithEagerRelationshipsIsEnabled() throws Exception {
        when(alunnoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAlunnoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(alunnoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlunnosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(alunnoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAlunnoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(alunnoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAlunno() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get the alunno
        restAlunnoMockMvc
            .perform(get(ENTITY_API_URL_ID, alunno.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(alunno.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.cognome").value(DEFAULT_COGNOME))
            .andExpect(jsonPath("$.dataNascita").value(DEFAULT_DATA_NASCITA.toString()));
    }

    @Test
    @Transactional
    void getAlunnosByIdFiltering() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        Long id = alunno.getId();

        defaultAlunnoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAlunnoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAlunnoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAlunnosByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where nome equals to
        defaultAlunnoFiltering("nome.equals=" + DEFAULT_NOME, "nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllAlunnosByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where nome in
        defaultAlunnoFiltering("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME, "nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllAlunnosByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where nome is not null
        defaultAlunnoFiltering("nome.specified=true", "nome.specified=false");
    }

    @Test
    @Transactional
    void getAllAlunnosByNomeContainsSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where nome contains
        defaultAlunnoFiltering("nome.contains=" + DEFAULT_NOME, "nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllAlunnosByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where nome does not contain
        defaultAlunnoFiltering("nome.doesNotContain=" + UPDATED_NOME, "nome.doesNotContain=" + DEFAULT_NOME);
    }

    @Test
    @Transactional
    void getAllAlunnosByCognomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where cognome equals to
        defaultAlunnoFiltering("cognome.equals=" + DEFAULT_COGNOME, "cognome.equals=" + UPDATED_COGNOME);
    }

    @Test
    @Transactional
    void getAllAlunnosByCognomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where cognome in
        defaultAlunnoFiltering("cognome.in=" + DEFAULT_COGNOME + "," + UPDATED_COGNOME, "cognome.in=" + UPDATED_COGNOME);
    }

    @Test
    @Transactional
    void getAllAlunnosByCognomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where cognome is not null
        defaultAlunnoFiltering("cognome.specified=true", "cognome.specified=false");
    }

    @Test
    @Transactional
    void getAllAlunnosByCognomeContainsSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where cognome contains
        defaultAlunnoFiltering("cognome.contains=" + DEFAULT_COGNOME, "cognome.contains=" + UPDATED_COGNOME);
    }

    @Test
    @Transactional
    void getAllAlunnosByCognomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where cognome does not contain
        defaultAlunnoFiltering("cognome.doesNotContain=" + UPDATED_COGNOME, "cognome.doesNotContain=" + DEFAULT_COGNOME);
    }

    @Test
    @Transactional
    void getAllAlunnosByDataNascitaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where dataNascita equals to
        defaultAlunnoFiltering("dataNascita.equals=" + DEFAULT_DATA_NASCITA, "dataNascita.equals=" + UPDATED_DATA_NASCITA);
    }

    @Test
    @Transactional
    void getAllAlunnosByDataNascitaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where dataNascita in
        defaultAlunnoFiltering(
            "dataNascita.in=" + DEFAULT_DATA_NASCITA + "," + UPDATED_DATA_NASCITA,
            "dataNascita.in=" + UPDATED_DATA_NASCITA
        );
    }

    @Test
    @Transactional
    void getAllAlunnosByDataNascitaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where dataNascita is not null
        defaultAlunnoFiltering("dataNascita.specified=true", "dataNascita.specified=false");
    }

    @Test
    @Transactional
    void getAllAlunnosByDataNascitaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where dataNascita is greater than or equal to
        defaultAlunnoFiltering(
            "dataNascita.greaterThanOrEqual=" + DEFAULT_DATA_NASCITA,
            "dataNascita.greaterThanOrEqual=" + UPDATED_DATA_NASCITA
        );
    }

    @Test
    @Transactional
    void getAllAlunnosByDataNascitaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where dataNascita is less than or equal to
        defaultAlunnoFiltering(
            "dataNascita.lessThanOrEqual=" + DEFAULT_DATA_NASCITA,
            "dataNascita.lessThanOrEqual=" + SMALLER_DATA_NASCITA
        );
    }

    @Test
    @Transactional
    void getAllAlunnosByDataNascitaIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where dataNascita is less than
        defaultAlunnoFiltering("dataNascita.lessThan=" + UPDATED_DATA_NASCITA, "dataNascita.lessThan=" + DEFAULT_DATA_NASCITA);
    }

    @Test
    @Transactional
    void getAllAlunnosByDataNascitaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        // Get all the alunnoList where dataNascita is greater than
        defaultAlunnoFiltering("dataNascita.greaterThan=" + SMALLER_DATA_NASCITA, "dataNascita.greaterThan=" + DEFAULT_DATA_NASCITA);
    }

    @Test
    @Transactional
    void getAllAlunnosByClasseIsEqualToSomething() throws Exception {
        Classe classe;
        if (TestUtil.findAll(em, Classe.class).isEmpty()) {
            alunnoRepository.saveAndFlush(alunno);
            classe = ClasseResourceIT.createEntity();
        } else {
            classe = TestUtil.findAll(em, Classe.class).get(0);
        }
        em.persist(classe);
        em.flush();
        alunno.setClasse(classe);
        alunnoRepository.saveAndFlush(alunno);
        Long classeId = classe.getId();
        // Get all the alunnoList where classe equals to classeId
        defaultAlunnoShouldBeFound("classeId.equals=" + classeId);

        // Get all the alunnoList where classe equals to (classeId + 1)
        defaultAlunnoShouldNotBeFound("classeId.equals=" + (classeId + 1));
    }

    private void defaultAlunnoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAlunnoShouldBeFound(shouldBeFound);
        defaultAlunnoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAlunnoShouldBeFound(String filter) throws Exception {
        restAlunnoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alunno.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].cognome").value(hasItem(DEFAULT_COGNOME)))
            .andExpect(jsonPath("$.[*].dataNascita").value(hasItem(DEFAULT_DATA_NASCITA.toString())));

        // Check, that the count call also returns 1
        restAlunnoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAlunnoShouldNotBeFound(String filter) throws Exception {
        restAlunnoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAlunnoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAlunno() throws Exception {
        // Get the alunno
        restAlunnoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAlunno() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alunno
        Alunno updatedAlunno = alunnoRepository.findById(alunno.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAlunno are not directly saved in db
        em.detach(updatedAlunno);
        updatedAlunno.nome(UPDATED_NOME).cognome(UPDATED_COGNOME).dataNascita(UPDATED_DATA_NASCITA);
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(updatedAlunno);

        restAlunnoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, alunnoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alunnoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Alunno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlunnoToMatchAllProperties(updatedAlunno);
    }

    @Test
    @Transactional
    void putNonExistingAlunno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alunno.setId(longCount.incrementAndGet());

        // Create the Alunno
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlunnoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, alunnoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alunnoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alunno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAlunno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alunno.setId(longCount.incrementAndGet());

        // Create the Alunno
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlunnoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(alunnoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alunno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAlunno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alunno.setId(longCount.incrementAndGet());

        // Create the Alunno
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlunnoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alunnoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alunno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAlunnoWithPatch() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alunno using partial update
        Alunno partialUpdatedAlunno = new Alunno();
        partialUpdatedAlunno.setId(alunno.getId());

        partialUpdatedAlunno.nome(UPDATED_NOME).cognome(UPDATED_COGNOME);

        restAlunnoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlunno.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlunno))
            )
            .andExpect(status().isOk());

        // Validate the Alunno in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlunnoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAlunno, alunno), getPersistedAlunno(alunno));
    }

    @Test
    @Transactional
    void fullUpdateAlunnoWithPatch() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alunno using partial update
        Alunno partialUpdatedAlunno = new Alunno();
        partialUpdatedAlunno.setId(alunno.getId());

        partialUpdatedAlunno.nome(UPDATED_NOME).cognome(UPDATED_COGNOME).dataNascita(UPDATED_DATA_NASCITA);

        restAlunnoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlunno.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlunno))
            )
            .andExpect(status().isOk());

        // Validate the Alunno in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlunnoUpdatableFieldsEquals(partialUpdatedAlunno, getPersistedAlunno(partialUpdatedAlunno));
    }

    @Test
    @Transactional
    void patchNonExistingAlunno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alunno.setId(longCount.incrementAndGet());

        // Create the Alunno
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlunnoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, alunnoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alunnoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alunno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAlunno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alunno.setId(longCount.incrementAndGet());

        // Create the Alunno
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlunnoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alunnoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alunno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAlunno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alunno.setId(longCount.incrementAndGet());

        // Create the Alunno
        AlunnoDTO alunnoDTO = alunnoMapper.toDto(alunno);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlunnoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(alunnoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alunno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAlunno() throws Exception {
        // Initialize the database
        insertedAlunno = alunnoRepository.saveAndFlush(alunno);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the alunno
        restAlunnoMockMvc
            .perform(delete(ENTITY_API_URL_ID, alunno.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return alunnoRepository.count();
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

    protected Alunno getPersistedAlunno(Alunno alunno) {
        return alunnoRepository.findById(alunno.getId()).orElseThrow();
    }

    protected void assertPersistedAlunnoToMatchAllProperties(Alunno expectedAlunno) {
        assertAlunnoAllPropertiesEquals(expectedAlunno, getPersistedAlunno(expectedAlunno));
    }

    protected void assertPersistedAlunnoToMatchUpdatableProperties(Alunno expectedAlunno) {
        assertAlunnoAllUpdatablePropertiesEquals(expectedAlunno, getPersistedAlunno(expectedAlunno));
    }
}
