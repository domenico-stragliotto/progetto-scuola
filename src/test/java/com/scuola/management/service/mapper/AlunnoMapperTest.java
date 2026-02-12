package com.scuola.management.service.mapper;

import static com.scuola.management.domain.AlunnoAsserts.*;
import static com.scuola.management.domain.AlunnoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlunnoMapperTest {

    private AlunnoMapper alunnoMapper;

    @BeforeEach
    void setUp() {
        alunnoMapper = new AlunnoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAlunnoSample1();
        var actual = alunnoMapper.toEntity(alunnoMapper.toDto(expected));
        assertAlunnoAllPropertiesEquals(expected, actual);
    }
}
