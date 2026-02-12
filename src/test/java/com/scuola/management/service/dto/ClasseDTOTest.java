package com.scuola.management.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.scuola.management.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClasseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClasseDTO.class);
        ClasseDTO classeDTO1 = new ClasseDTO();
        classeDTO1.setId(1L);
        ClasseDTO classeDTO2 = new ClasseDTO();
        assertThat(classeDTO1).isNotEqualTo(classeDTO2);
        classeDTO2.setId(classeDTO1.getId());
        assertThat(classeDTO1).isEqualTo(classeDTO2);
        classeDTO2.setId(2L);
        assertThat(classeDTO1).isNotEqualTo(classeDTO2);
        classeDTO1.setId(null);
        assertThat(classeDTO1).isNotEqualTo(classeDTO2);
    }
}
