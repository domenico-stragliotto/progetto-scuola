package com.scuola.management.service.impl;

import com.scuola.management.domain.CompitoInClasse;
import com.scuola.management.repository.CompitoInClasseRepository;
import com.scuola.management.service.CompitoInClasseService;
import com.scuola.management.service.dto.CompitoInClasseDTO;
import com.scuola.management.service.mapper.CompitoInClasseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.scuola.management.domain.CompitoInClasse}.
 */
@Service
@Transactional
public class CompitoInClasseServiceImpl implements CompitoInClasseService {

    private static final Logger LOG = LoggerFactory.getLogger(CompitoInClasseServiceImpl.class);

    private final CompitoInClasseRepository compitoInClasseRepository;

    private final CompitoInClasseMapper compitoInClasseMapper;

    public CompitoInClasseServiceImpl(CompitoInClasseRepository compitoInClasseRepository, CompitoInClasseMapper compitoInClasseMapper) {
        this.compitoInClasseRepository = compitoInClasseRepository;
        this.compitoInClasseMapper = compitoInClasseMapper;
    }

    @Override
    public CompitoInClasseDTO save(CompitoInClasseDTO compitoInClasseDTO) {
        LOG.debug("Request to save CompitoInClasse : {}", compitoInClasseDTO);
        CompitoInClasse compitoInClasse = compitoInClasseMapper.toEntity(compitoInClasseDTO);
        compitoInClasse = compitoInClasseRepository.save(compitoInClasse);
        return compitoInClasseMapper.toDto(compitoInClasse);
    }

    @Override
    public CompitoInClasseDTO update(CompitoInClasseDTO compitoInClasseDTO) {
        LOG.debug("Request to update CompitoInClasse : {}", compitoInClasseDTO);
        CompitoInClasse compitoInClasse = compitoInClasseMapper.toEntity(compitoInClasseDTO);
        compitoInClasse = compitoInClasseRepository.save(compitoInClasse);
        return compitoInClasseMapper.toDto(compitoInClasse);
    }

    @Override
    public Optional<CompitoInClasseDTO> partialUpdate(CompitoInClasseDTO compitoInClasseDTO) {
        LOG.debug("Request to partially update CompitoInClasse : {}", compitoInClasseDTO);

        return compitoInClasseRepository
            .findById(compitoInClasseDTO.getId())
            .map(existingCompitoInClasse -> {
                compitoInClasseMapper.partialUpdate(existingCompitoInClasse, compitoInClasseDTO);

                return existingCompitoInClasse;
            })
            .map(compitoInClasseRepository::save)
            .map(compitoInClasseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompitoInClasseDTO> findOne(Long id) {
        LOG.debug("Request to get CompitoInClasse : {}", id);
        return compitoInClasseRepository.findById(id).map(compitoInClasseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CompitoInClasse : {}", id);
        compitoInClasseRepository.deleteById(id);
    }
}
