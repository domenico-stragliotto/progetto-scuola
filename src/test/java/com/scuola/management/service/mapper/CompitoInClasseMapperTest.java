package com.scuola.management.service.mapper;

import static com.scuola.management.domain.CompitoInClasseAsserts.*;
import static com.scuola.management.domain.CompitoInClasseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompitoInClasseMapperTest {

    private CompitoInClasseMapper compitoInClasseMapper;

    @BeforeEach
    void setUp() {
        compitoInClasseMapper = new CompitoInClasseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCompitoInClasseSample1();
        var actual = compitoInClasseMapper.toEntity(compitoInClasseMapper.toDto(expected));
        assertCompitoInClasseAllPropertiesEquals(expected, actual);
    }
}
