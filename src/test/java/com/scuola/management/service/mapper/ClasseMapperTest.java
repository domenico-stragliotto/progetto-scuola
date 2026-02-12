package com.scuola.management.service.mapper;

import static com.scuola.management.domain.ClasseAsserts.*;
import static com.scuola.management.domain.ClasseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClasseMapperTest {

    private ClasseMapper classeMapper;

    @BeforeEach
    void setUp() {
        classeMapper = new ClasseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClasseSample1();
        var actual = classeMapper.toEntity(classeMapper.toDto(expected));
        assertClasseAllPropertiesEquals(expected, actual);
    }
}
