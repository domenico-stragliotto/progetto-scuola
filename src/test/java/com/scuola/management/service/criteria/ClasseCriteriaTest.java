package com.scuola.management.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClasseCriteriaTest {

    @Test
    void newClasseCriteriaHasAllFiltersNullTest() {
        var classeCriteria = new ClasseCriteria();
        assertThat(classeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void classeCriteriaFluentMethodsCreatesFiltersTest() {
        var classeCriteria = new ClasseCriteria();

        setAllFilters(classeCriteria);

        assertThat(classeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void classeCriteriaCopyCreatesNullFilterTest() {
        var classeCriteria = new ClasseCriteria();
        var copy = classeCriteria.copy();

        assertThat(classeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(classeCriteria)
        );
    }

    @Test
    void classeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var classeCriteria = new ClasseCriteria();
        setAllFilters(classeCriteria);

        var copy = classeCriteria.copy();

        assertThat(classeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(classeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var classeCriteria = new ClasseCriteria();

        assertThat(classeCriteria).hasToString("ClasseCriteria{}");
    }

    private static void setAllFilters(ClasseCriteria classeCriteria) {
        classeCriteria.id();
        classeCriteria.numero();
        classeCriteria.sezione();
        classeCriteria.note();
        classeCriteria.distinct();
    }

    private static Condition<ClasseCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNumero()) &&
                condition.apply(criteria.getSezione()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ClasseCriteria> copyFiltersAre(ClasseCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNumero(), copy.getNumero()) &&
                condition.apply(criteria.getSezione(), copy.getSezione()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
