package com.scuola.management.domain;

import static com.scuola.management.domain.AlunnoTestSamples.*;
import static com.scuola.management.domain.CompitoInClasseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.scuola.management.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompitoInClasseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompitoInClasse.class);
        CompitoInClasse compitoInClasse1 = getCompitoInClasseSample1();
        CompitoInClasse compitoInClasse2 = new CompitoInClasse();
        assertThat(compitoInClasse1).isNotEqualTo(compitoInClasse2);

        compitoInClasse2.setId(compitoInClasse1.getId());
        assertThat(compitoInClasse1).isEqualTo(compitoInClasse2);

        compitoInClasse2 = getCompitoInClasseSample2();
        assertThat(compitoInClasse1).isNotEqualTo(compitoInClasse2);
    }

    @Test
    void alunnoTest() {
        CompitoInClasse compitoInClasse = getCompitoInClasseRandomSampleGenerator();
        Alunno alunnoBack = getAlunnoRandomSampleGenerator();

        compitoInClasse.setAlunno(alunnoBack);
        assertThat(compitoInClasse.getAlunno()).isEqualTo(alunnoBack);

        compitoInClasse.alunno(null);
        assertThat(compitoInClasse.getAlunno()).isNull();
    }
}
