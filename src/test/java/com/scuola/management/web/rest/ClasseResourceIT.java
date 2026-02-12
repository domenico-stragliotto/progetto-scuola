package com.scuola.management.web.rest;

import static com.scuola.management.domain.ClasseAsserts.*;
import static com.scuola.management.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scuola.management.IntegrationTest;
import com.scuola.management.domain.Classe;
import com.scuola.management.repository.ClasseRepository;
import com.scuola.management.service.dto.ClasseDTO;
import com.scuola.management.service.mapper.ClasseMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ClasseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClasseResourceIT {

    private static final Integer DEFAULT_NUMERO = 1;
    private static final Integer UPDATED_NUMERO = 2;
    private static final Integer SMALLER_NUMERO = 1 - 1;

    private static final String DEFAULT_SEZIONE = "A";
    private static final String UPDATED_SEZIONE = "B";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/classes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private ClasseMapper classeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClasseMockMvc;

    private Classe classe;

    private Classe insertedClasse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Classe createEntity() {
        return new Classe().numero(DEFAULT_NUMERO).sezione(DEFAULT_SEZIONE).note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Classe createUpdatedEntity() {
        return new Classe().numero(UPDATED_NUMERO).sezione(UPDATED_SEZIONE).note(UPDATED_NOTE);
    }

    @BeforeEach
    void initTest() {
        classe = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClasse != null) {
            classeRepository.delete(insertedClasse);
            insertedClasse = null;
        }
    }

    @Test
    @Transactional
    void createClasse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Classe
        ClasseDTO classeDTO = classeMapper.toDto(classe);
        var returnedClasseDTO = om.readValue(
            restClasseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClasseDTO.class
        );

        // Validate the Classe in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClasse = classeMapper.toEntity(returnedClasseDTO);
        assertClasseUpdatableFieldsEquals(returnedClasse, getPersistedClasse(returnedClasse));

        insertedClasse = returnedClasse;
    }

    @Test
    @Transactional
    void createClasseWithExistingId() throws Exception {
        // Create the Classe with an existing ID
        classe.setId(1L);
        ClasseDTO classeDTO = classeMapper.toDto(classe);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClasseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Classe in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classe.setNumero(null);

        // Create the Classe, which fails.
        ClasseDTO classeDTO = classeMapper.toDto(classe);

        restClasseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSezioneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classe.setSezione(null);

        // Create the Classe, which fails.
        ClasseDTO classeDTO = classeMapper.toDto(classe);

        restClasseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClasses() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList
        restClasseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classe.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].sezione").value(hasItem(DEFAULT_SEZIONE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getClasse() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get the classe
        restClasseMockMvc
            .perform(get(ENTITY_API_URL_ID, classe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(classe.getId().intValue()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.sezione").value(DEFAULT_SEZIONE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getClassesByIdFiltering() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        Long id = classe.getId();

        defaultClasseFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClasseFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClasseFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClassesByNumeroIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where numero equals to
        defaultClasseFiltering("numero.equals=" + DEFAULT_NUMERO, "numero.equals=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllClassesByNumeroIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where numero in
        defaultClasseFiltering("numero.in=" + DEFAULT_NUMERO + "," + UPDATED_NUMERO, "numero.in=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllClassesByNumeroIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where numero is not null
        defaultClasseFiltering("numero.specified=true", "numero.specified=false");
    }

    @Test
    @Transactional
    void getAllClassesByNumeroIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where numero is greater than or equal to
        defaultClasseFiltering("numero.greaterThanOrEqual=" + DEFAULT_NUMERO, "numero.greaterThanOrEqual=" + (DEFAULT_NUMERO + 1));
    }

    @Test
    @Transactional
    void getAllClassesByNumeroIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where numero is less than or equal to
        defaultClasseFiltering("numero.lessThanOrEqual=" + DEFAULT_NUMERO, "numero.lessThanOrEqual=" + SMALLER_NUMERO);
    }

    @Test
    @Transactional
    void getAllClassesByNumeroIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where numero is less than
        defaultClasseFiltering("numero.lessThan=" + (DEFAULT_NUMERO + 1), "numero.lessThan=" + DEFAULT_NUMERO);
    }

    @Test
    @Transactional
    void getAllClassesByNumeroIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where numero is greater than
        defaultClasseFiltering("numero.greaterThan=" + SMALLER_NUMERO, "numero.greaterThan=" + DEFAULT_NUMERO);
    }

    @Test
    @Transactional
    void getAllClassesBySezioneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where sezione equals to
        defaultClasseFiltering("sezione.equals=" + DEFAULT_SEZIONE, "sezione.equals=" + UPDATED_SEZIONE);
    }

    @Test
    @Transactional
    void getAllClassesBySezioneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where sezione in
        defaultClasseFiltering("sezione.in=" + DEFAULT_SEZIONE + "," + UPDATED_SEZIONE, "sezione.in=" + UPDATED_SEZIONE);
    }

    @Test
    @Transactional
    void getAllClassesBySezioneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where sezione is not null
        defaultClasseFiltering("sezione.specified=true", "sezione.specified=false");
    }

    @Test
    @Transactional
    void getAllClassesBySezioneContainsSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where sezione contains
        defaultClasseFiltering("sezione.contains=" + DEFAULT_SEZIONE, "sezione.contains=" + UPDATED_SEZIONE);
    }

    @Test
    @Transactional
    void getAllClassesBySezioneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where sezione does not contain
        defaultClasseFiltering("sezione.doesNotContain=" + UPDATED_SEZIONE, "sezione.doesNotContain=" + DEFAULT_SEZIONE);
    }

    @Test
    @Transactional
    void getAllClassesByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where note equals to
        defaultClasseFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllClassesByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where note in
        defaultClasseFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllClassesByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where note is not null
        defaultClasseFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllClassesByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where note contains
        defaultClasseFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllClassesByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        // Get all the classeList where note does not contain
        defaultClasseFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    private void defaultClasseFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClasseShouldBeFound(shouldBeFound);
        defaultClasseShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClasseShouldBeFound(String filter) throws Exception {
        restClasseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classe.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].sezione").value(hasItem(DEFAULT_SEZIONE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));

        // Check, that the count call also returns 1
        restClasseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClasseShouldNotBeFound(String filter) throws Exception {
        restClasseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClasseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClasse() throws Exception {
        // Get the classe
        restClasseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClasse() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classe
        Classe updatedClasse = classeRepository.findById(classe.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClasse are not directly saved in db
        em.detach(updatedClasse);
        updatedClasse.numero(UPDATED_NUMERO).sezione(UPDATED_SEZIONE).note(UPDATED_NOTE);
        ClasseDTO classeDTO = classeMapper.toDto(updatedClasse);

        restClasseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Classe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClasseToMatchAllProperties(updatedClasse);
    }

    @Test
    @Transactional
    void putNonExistingClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classe.setId(longCount.incrementAndGet());

        // Create the Classe
        ClasseDTO classeDTO = classeMapper.toDto(classe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClasseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Classe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classe.setId(longCount.incrementAndGet());

        // Create the Classe
        ClasseDTO classeDTO = classeMapper.toDto(classe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClasseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Classe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classe.setId(longCount.incrementAndGet());

        // Create the Classe
        ClasseDTO classeDTO = classeMapper.toDto(classe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClasseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Classe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClasseWithPatch() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classe using partial update
        Classe partialUpdatedClasse = new Classe();
        partialUpdatedClasse.setId(classe.getId());

        partialUpdatedClasse.numero(UPDATED_NUMERO).note(UPDATED_NOTE);

        restClasseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClasse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClasse))
            )
            .andExpect(status().isOk());

        // Validate the Classe in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClasseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClasse, classe), getPersistedClasse(classe));
    }

    @Test
    @Transactional
    void fullUpdateClasseWithPatch() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classe using partial update
        Classe partialUpdatedClasse = new Classe();
        partialUpdatedClasse.setId(classe.getId());

        partialUpdatedClasse.numero(UPDATED_NUMERO).sezione(UPDATED_SEZIONE).note(UPDATED_NOTE);

        restClasseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClasse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClasse))
            )
            .andExpect(status().isOk());

        // Validate the Classe in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClasseUpdatableFieldsEquals(partialUpdatedClasse, getPersistedClasse(partialUpdatedClasse));
    }

    @Test
    @Transactional
    void patchNonExistingClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classe.setId(longCount.incrementAndGet());

        // Create the Classe
        ClasseDTO classeDTO = classeMapper.toDto(classe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClasseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, classeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(classeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Classe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classe.setId(longCount.incrementAndGet());

        // Create the Classe
        ClasseDTO classeDTO = classeMapper.toDto(classe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClasseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(classeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Classe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClasse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classe.setId(longCount.incrementAndGet());

        // Create the Classe
        ClasseDTO classeDTO = classeMapper.toDto(classe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClasseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(classeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Classe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClasse() throws Exception {
        // Initialize the database
        insertedClasse = classeRepository.saveAndFlush(classe);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the classe
        restClasseMockMvc
            .perform(delete(ENTITY_API_URL_ID, classe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return classeRepository.count();
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

    protected Classe getPersistedClasse(Classe classe) {
        return classeRepository.findById(classe.getId()).orElseThrow();
    }

    protected void assertPersistedClasseToMatchAllProperties(Classe expectedClasse) {
        assertClasseAllPropertiesEquals(expectedClasse, getPersistedClasse(expectedClasse));
    }

    protected void assertPersistedClasseToMatchUpdatableProperties(Classe expectedClasse) {
        assertClasseAllUpdatablePropertiesEquals(expectedClasse, getPersistedClasse(expectedClasse));
    }
}
