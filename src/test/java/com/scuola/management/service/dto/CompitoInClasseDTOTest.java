package com.scuola.management.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.scuola.management.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompitoInClasseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompitoInClasseDTO.class);
        CompitoInClasseDTO compitoInClasseDTO1 = new CompitoInClasseDTO();
        compitoInClasseDTO1.setId(1L);
        CompitoInClasseDTO compitoInClasseDTO2 = new CompitoInClasseDTO();
        assertThat(compitoInClasseDTO1).isNotEqualTo(compitoInClasseDTO2);
        compitoInClasseDTO2.setId(compitoInClasseDTO1.getId());
        assertThat(compitoInClasseDTO1).isEqualTo(compitoInClasseDTO2);
        compitoInClasseDTO2.setId(2L);
        assertThat(compitoInClasseDTO1).isNotEqualTo(compitoInClasseDTO2);
        compitoInClasseDTO1.setId(null);
        assertThat(compitoInClasseDTO1).isNotEqualTo(compitoInClasseDTO2);
    }
}
