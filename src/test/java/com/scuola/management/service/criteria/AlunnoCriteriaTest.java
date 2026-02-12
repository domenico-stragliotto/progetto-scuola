package com.scuola.management.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AlunnoCriteriaTest {

    @Test
    void newAlunnoCriteriaHasAllFiltersNullTest() {
        var alunnoCriteria = new AlunnoCriteria();
        assertThat(alunnoCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void alunnoCriteriaFluentMethodsCreatesFiltersTest() {
        var alunnoCriteria = new AlunnoCriteria();

        setAllFilters(alunnoCriteria);

        assertThat(alunnoCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void alunnoCriteriaCopyCreatesNullFilterTest() {
        var alunnoCriteria = new AlunnoCriteria();
        var copy = alunnoCriteria.copy();

        assertThat(alunnoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(alunnoCriteria)
        );
    }

    @Test
    void alunnoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var alunnoCriteria = new AlunnoCriteria();
        setAllFilters(alunnoCriteria);

        var copy = alunnoCriteria.copy();

        assertThat(alunnoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(alunnoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var alunnoCriteria = new AlunnoCriteria();

        assertThat(alunnoCriteria).hasToString("AlunnoCriteria{}");
    }

    private static void setAllFilters(AlunnoCriteria alunnoCriteria) {
        alunnoCriteria.id();
        alunnoCriteria.nome();
        alunnoCriteria.cognome();
        alunnoCriteria.dataNascita();
        alunnoCriteria.compitoInClasseId();
        alunnoCriteria.classeId();
        alunnoCriteria.distinct();
    }

    private static Condition<AlunnoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNome()) &&
                condition.apply(criteria.getCognome()) &&
                condition.apply(criteria.getDataNascita()) &&
                condition.apply(criteria.getCompitoInClasseId()) &&
                condition.apply(criteria.getClasseId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AlunnoCriteria> copyFiltersAre(AlunnoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNome(), copy.getNome()) &&
                condition.apply(criteria.getCognome(), copy.getCognome()) &&
                condition.apply(criteria.getDataNascita(), copy.getDataNascita()) &&
                condition.apply(criteria.getCompitoInClasseId(), copy.getCompitoInClasseId()) &&
                condition.apply(criteria.getClasseId(), copy.getClasseId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
