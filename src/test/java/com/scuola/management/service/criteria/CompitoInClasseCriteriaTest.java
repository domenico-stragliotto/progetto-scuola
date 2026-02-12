package com.scuola.management.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CompitoInClasseCriteriaTest {

    @Test
    void newCompitoInClasseCriteriaHasAllFiltersNullTest() {
        var compitoInClasseCriteria = new CompitoInClasseCriteria();
        assertThat(compitoInClasseCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void compitoInClasseCriteriaFluentMethodsCreatesFiltersTest() {
        var compitoInClasseCriteria = new CompitoInClasseCriteria();

        setAllFilters(compitoInClasseCriteria);

        assertThat(compitoInClasseCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void compitoInClasseCriteriaCopyCreatesNullFilterTest() {
        var compitoInClasseCriteria = new CompitoInClasseCriteria();
        var copy = compitoInClasseCriteria.copy();

        assertThat(compitoInClasseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(compitoInClasseCriteria)
        );
    }

    @Test
    void compitoInClasseCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var compitoInClasseCriteria = new CompitoInClasseCriteria();
        setAllFilters(compitoInClasseCriteria);

        var copy = compitoInClasseCriteria.copy();

        assertThat(compitoInClasseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(compitoInClasseCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var compitoInClasseCriteria = new CompitoInClasseCriteria();

        assertThat(compitoInClasseCriteria).hasToString("CompitoInClasseCriteria{}");
    }

    private static void setAllFilters(CompitoInClasseCriteria compitoInClasseCriteria) {
        compitoInClasseCriteria.id();
        compitoInClasseCriteria.data();
        compitoInClasseCriteria.materia();
        compitoInClasseCriteria.risultato();
        compitoInClasseCriteria.alunnoId();
        compitoInClasseCriteria.distinct();
    }

    private static Condition<CompitoInClasseCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getData()) &&
                condition.apply(criteria.getMateria()) &&
                condition.apply(criteria.getRisultato()) &&
                condition.apply(criteria.getAlunnoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CompitoInClasseCriteria> copyFiltersAre(
        CompitoInClasseCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getData(), copy.getData()) &&
                condition.apply(criteria.getMateria(), copy.getMateria()) &&
                condition.apply(criteria.getRisultato(), copy.getRisultato()) &&
                condition.apply(criteria.getAlunnoId(), copy.getAlunnoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
