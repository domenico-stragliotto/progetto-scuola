package com.scuola.management.service.criteria;

import com.scuola.management.domain.enumeration.Materia;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.scuola.management.domain.CompitoInClasse} entity. This class is used
 * in {@link com.scuola.management.web.rest.CompitoInClasseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /compito-in-classes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompitoInClasseCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Materia
     */
    public static class MateriaFilter extends Filter<Materia> {

        public MateriaFilter() {}

        public MateriaFilter(MateriaFilter filter) {
            super(filter);
        }

        @Override
        public MateriaFilter copy() {
            return new MateriaFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter data;

    private MateriaFilter materia;

    private DoubleFilter risultato;

    private LongFilter alunnoId;

    private Boolean distinct;

    public CompitoInClasseCriteria() {}

    public CompitoInClasseCriteria(CompitoInClasseCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.data = other.optionalData().map(LocalDateFilter::copy).orElse(null);
        this.materia = other.optionalMateria().map(MateriaFilter::copy).orElse(null);
        this.risultato = other.optionalRisultato().map(DoubleFilter::copy).orElse(null);
        this.alunnoId = other.optionalAlunnoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CompitoInClasseCriteria copy() {
        return new CompitoInClasseCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getData() {
        return data;
    }

    public Optional<LocalDateFilter> optionalData() {
        return Optional.ofNullable(data);
    }

    public LocalDateFilter data() {
        if (data == null) {
            setData(new LocalDateFilter());
        }
        return data;
    }

    public void setData(LocalDateFilter data) {
        this.data = data;
    }

    public MateriaFilter getMateria() {
        return materia;
    }

    public Optional<MateriaFilter> optionalMateria() {
        return Optional.ofNullable(materia);
    }

    public MateriaFilter materia() {
        if (materia == null) {
            setMateria(new MateriaFilter());
        }
        return materia;
    }

    public void setMateria(MateriaFilter materia) {
        this.materia = materia;
    }

    public DoubleFilter getRisultato() {
        return risultato;
    }

    public Optional<DoubleFilter> optionalRisultato() {
        return Optional.ofNullable(risultato);
    }

    public DoubleFilter risultato() {
        if (risultato == null) {
            setRisultato(new DoubleFilter());
        }
        return risultato;
    }

    public void setRisultato(DoubleFilter risultato) {
        this.risultato = risultato;
    }

    public LongFilter getAlunnoId() {
        return alunnoId;
    }

    public Optional<LongFilter> optionalAlunnoId() {
        return Optional.ofNullable(alunnoId);
    }

    public LongFilter alunnoId() {
        if (alunnoId == null) {
            setAlunnoId(new LongFilter());
        }
        return alunnoId;
    }

    public void setAlunnoId(LongFilter alunnoId) {
        this.alunnoId = alunnoId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CompitoInClasseCriteria that = (CompitoInClasseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(data, that.data) &&
            Objects.equals(materia, that.materia) &&
            Objects.equals(risultato, that.risultato) &&
            Objects.equals(alunnoId, that.alunnoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, materia, risultato, alunnoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompitoInClasseCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalData().map(f -> "data=" + f + ", ").orElse("") +
            optionalMateria().map(f -> "materia=" + f + ", ").orElse("") +
            optionalRisultato().map(f -> "risultato=" + f + ", ").orElse("") +
            optionalAlunnoId().map(f -> "alunnoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
