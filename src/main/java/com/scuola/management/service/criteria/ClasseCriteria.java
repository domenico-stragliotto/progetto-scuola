package com.scuola.management.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.scuola.management.domain.Classe} entity. This class is used
 * in {@link com.scuola.management.web.rest.ClasseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /classes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClasseCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter numero;

    private StringFilter sezione;

    private StringFilter note;

    private Boolean distinct;

    public ClasseCriteria() {}

    public ClasseCriteria(ClasseCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.numero = other.optionalNumero().map(IntegerFilter::copy).orElse(null);
        this.sezione = other.optionalSezione().map(StringFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ClasseCriteria copy() {
        return new ClasseCriteria(this);
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

    public IntegerFilter getNumero() {
        return numero;
    }

    public Optional<IntegerFilter> optionalNumero() {
        return Optional.ofNullable(numero);
    }

    public IntegerFilter numero() {
        if (numero == null) {
            setNumero(new IntegerFilter());
        }
        return numero;
    }

    public void setNumero(IntegerFilter numero) {
        this.numero = numero;
    }

    public StringFilter getSezione() {
        return sezione;
    }

    public Optional<StringFilter> optionalSezione() {
        return Optional.ofNullable(sezione);
    }

    public StringFilter sezione() {
        if (sezione == null) {
            setSezione(new StringFilter());
        }
        return sezione;
    }

    public void setSezione(StringFilter sezione) {
        this.sezione = sezione;
    }

    public StringFilter getNote() {
        return note;
    }

    public Optional<StringFilter> optionalNote() {
        return Optional.ofNullable(note);
    }

    public StringFilter note() {
        if (note == null) {
            setNote(new StringFilter());
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
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
        final ClasseCriteria that = (ClasseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numero, that.numero) &&
            Objects.equals(sezione, that.sezione) &&
            Objects.equals(note, that.note) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numero, sezione, note, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClasseCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNumero().map(f -> "numero=" + f + ", ").orElse("") +
            optionalSezione().map(f -> "sezione=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
