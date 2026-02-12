package com.scuola.management.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.scuola.management.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AlunnoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AlunnoDTO.class);
        AlunnoDTO alunnoDTO1 = new AlunnoDTO();
        alunnoDTO1.setId(1L);
        AlunnoDTO alunnoDTO2 = new AlunnoDTO();
        assertThat(alunnoDTO1).isNotEqualTo(alunnoDTO2);
        alunnoDTO2.setId(alunnoDTO1.getId());
        assertThat(alunnoDTO1).isEqualTo(alunnoDTO2);
        alunnoDTO2.setId(2L);
        assertThat(alunnoDTO1).isNotEqualTo(alunnoDTO2);
        alunnoDTO1.setId(null);
        assertThat(alunnoDTO1).isNotEqualTo(alunnoDTO2);
    }
}
