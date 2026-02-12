package com.scuola.management.service;

import com.scuola.management.service.dto.CompitoInClasseDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.scuola.management.domain.CompitoInClasse}.
 */
public interface CompitoInClasseService {
    /**
     * Save a compitoInClasse.
     *
     * @param compitoInClasseDTO the entity to save.
     * @return the persisted entity.
     */
    CompitoInClasseDTO save(CompitoInClasseDTO compitoInClasseDTO);

    /**
     * Updates a compitoInClasse.
     *
     * @param compitoInClasseDTO the entity to update.
     * @return the persisted entity.
     */
    CompitoInClasseDTO update(CompitoInClasseDTO compitoInClasseDTO);

    /**
     * Partially updates a compitoInClasse.
     *
     * @param compitoInClasseDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CompitoInClasseDTO> partialUpdate(CompitoInClasseDTO compitoInClasseDTO);

    /**
     * Get the "id" compitoInClasse.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CompitoInClasseDTO> findOne(Long id);

    /**
     * Delete the "id" compitoInClasse.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
