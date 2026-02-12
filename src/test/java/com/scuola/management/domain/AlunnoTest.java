package com.scuola.management.domain;

import static com.scuola.management.domain.AlunnoTestSamples.*;
import static com.scuola.management.domain.ClasseTestSamples.*;
import static com.scuola.management.domain.CompitoInClasseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.scuola.management.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AlunnoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Alunno.class);
        Alunno alunno1 = getAlunnoSample1();
        Alunno alunno2 = new Alunno();
        assertThat(alunno1).isNotEqualTo(alunno2);

        alunno2.setId(alunno1.getId());
        assertThat(alunno1).isEqualTo(alunno2);

        alunno2 = getAlunnoSample2();
        assertThat(alunno1).isNotEqualTo(alunno2);
    }

    @Test
    void compitoInClasseTest() {
        Alunno alunno = getAlunnoRandomSampleGenerator();
        CompitoInClasse compitoInClasseBack = getCompitoInClasseRandomSampleGenerator();

        alunno.addCompitoInClasse(compitoInClasseBack);
        assertThat(alunno.getCompitoInClasses()).containsOnly(compitoInClasseBack);
        assertThat(compitoInClasseBack.getAlunno()).isEqualTo(alunno);

        alunno.removeCompitoInClasse(compitoInClasseBack);
        assertThat(alunno.getCompitoInClasses()).doesNotContain(compitoInClasseBack);
        assertThat(compitoInClasseBack.getAlunno()).isNull();

        alunno.compitoInClasses(new HashSet<>(Set.of(compitoInClasseBack)));
        assertThat(alunno.getCompitoInClasses()).containsOnly(compitoInClasseBack);
        assertThat(compitoInClasseBack.getAlunno()).isEqualTo(alunno);

        alunno.setCompitoInClasses(new HashSet<>());
        assertThat(alunno.getCompitoInClasses()).doesNotContain(compitoInClasseBack);
        assertThat(compitoInClasseBack.getAlunno()).isNull();
    }

    @Test
    void classeTest() {
        Alunno alunno = getAlunnoRandomSampleGenerator();
        Classe classeBack = getClasseRandomSampleGenerator();

        alunno.setClasse(classeBack);
        assertThat(alunno.getClasse()).isEqualTo(classeBack);

        alunno.classe(null);
        assertThat(alunno.getClasse()).isNull();
    }
}
