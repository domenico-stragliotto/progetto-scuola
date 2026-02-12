package com.scuola.management.service.impl;

import com.scuola.management.domain.Alunno;
import com.scuola.management.repository.AlunnoRepository;
import com.scuola.management.service.AlunnoService;
import com.scuola.management.service.dto.AlunnoDTO;
import com.scuola.management.service.mapper.AlunnoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.scuola.management.domain.Alunno}.
 */
@Service
@Transactional
public class AlunnoServiceImpl implements AlunnoService {

    private static final Logger LOG = LoggerFactory.getLogger(AlunnoServiceImpl.class);

    private final AlunnoRepository alunnoRepository;

    private final AlunnoMapper alunnoMapper;

    public AlunnoServiceImpl(AlunnoRepository alunnoRepository, AlunnoMapper alunnoMapper) {
        this.alunnoRepository = alunnoRepository;
        this.alunnoMapper = alunnoMapper;
    }

    @Override
    public AlunnoDTO save(AlunnoDTO alunnoDTO) {
        LOG.debug("Request to save Alunno : {}", alunnoDTO);
        Alunno alunno = alunnoMapper.toEntity(alunnoDTO);
        alunno = alunnoRepository.save(alunno);
        return alunnoMapper.toDto(alunno);
    }

    @Override
    public AlunnoDTO update(AlunnoDTO alunnoDTO) {
        LOG.debug("Request to update Alunno : {}", alunnoDTO);
        Alunno alunno = alunnoMapper.toEntity(alunnoDTO);
        alunno = alunnoRepository.save(alunno);
        return alunnoMapper.toDto(alunno);
    }

    @Override
    public Optional<AlunnoDTO> partialUpdate(AlunnoDTO alunnoDTO) {
        LOG.debug("Request to partially update Alunno : {}", alunnoDTO);

        return alunnoRepository
            .findById(alunnoDTO.getId())
            .map(existingAlunno -> {
                alunnoMapper.partialUpdate(existingAlunno, alunnoDTO);

                return existingAlunno;
            })
            .map(alunnoRepository::save)
            .map(alunnoMapper::toDto);
    }

    public Page<AlunnoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return alunnoRepository.findAllWithEagerRelationships(pageable).map(alunnoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AlunnoDTO> findOne(Long id) {
        LOG.debug("Request to get Alunno : {}", id);
        return alunnoRepository.findOneWithEagerRelationships(id).map(alunnoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Alunno : {}", id);
        alunnoRepository.deleteById(id);
    }
}
