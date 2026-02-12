package com.scuola.management.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.scuola.management.domain.Alunno} entity. This class is used
 * in {@link com.scuola.management.web.rest.AlunnoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /alunnos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlunnoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private StringFilter cognome;

    private LocalDateFilter dataNascita;

    private LongFilter compitoInClasseId;

    private LongFilter classeId;

    private Boolean distinct;

    public AlunnoCriteria() {}

    public AlunnoCriteria(AlunnoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nome = other.optionalNome().map(StringFilter::copy).orElse(null);
        this.cognome = other.optionalCognome().map(StringFilter::copy).orElse(null);
        this.dataNascita = other.optionalDataNascita().map(LocalDateFilter::copy).orElse(null);
        this.compitoInClasseId = other.optionalCompitoInClasseId().map(LongFilter::copy).orElse(null);
        this.classeId = other.optionalClasseId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AlunnoCriteria copy() {
        return new AlunnoCriteria(this);
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

    public StringFilter getNome() {
        return nome;
    }

    public Optional<StringFilter> optionalNome() {
        return Optional.ofNullable(nome);
    }

    public StringFilter nome() {
        if (nome == null) {
            setNome(new StringFilter());
        }
        return nome;
    }

    public void setNome(StringFilter nome) {
        this.nome = nome;
    }

    public StringFilter getCognome() {
        return cognome;
    }

    public Optional<StringFilter> optionalCognome() {
        return Optional.ofNullable(cognome);
    }

    public StringFilter cognome() {
        if (cognome == null) {
            setCognome(new StringFilter());
        }
        return cognome;
    }

    public void setCognome(StringFilter cognome) {
        this.cognome = cognome;
    }

    public LocalDateFilter getDataNascita() {
        return dataNascita;
    }

    public Optional<LocalDateFilter> optionalDataNascita() {
        return Optional.ofNullable(dataNascita);
    }

    public LocalDateFilter dataNascita() {
        if (dataNascita == null) {
            setDataNascita(new LocalDateFilter());
        }
        return dataNascita;
    }

    public void setDataNascita(LocalDateFilter dataNascita) {
        this.dataNascita = dataNascita;
    }

    public LongFilter getCompitoInClasseId() {
        return compitoInClasseId;
    }

    public Optional<LongFilter> optionalCompitoInClasseId() {
        return Optional.ofNullable(compitoInClasseId);
    }

    public LongFilter compitoInClasseId() {
        if (compitoInClasseId == null) {
            setCompitoInClasseId(new LongFilter());
        }
        return compitoInClasseId;
    }

    public void setCompitoInClasseId(LongFilter compitoInClasseId) {
        this.compitoInClasseId = compitoInClasseId;
    }

    public LongFilter getClasseId() {
        return classeId;
    }

    public Optional<LongFilter> optionalClasseId() {
        return Optional.ofNullable(classeId);
    }

    public LongFilter classeId() {
        if (classeId == null) {
            setClasseId(new LongFilter());
        }
        return classeId;
    }

    public void setClasseId(LongFilter classeId) {
        this.classeId = classeId;
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
        final AlunnoCriteria that = (AlunnoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(cognome, that.cognome) &&
            Objects.equals(dataNascita, that.dataNascita) &&
            Objects.equals(compitoInClasseId, that.compitoInClasseId) &&
            Objects.equals(classeId, that.classeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, cognome, dataNascita, compitoInClasseId, classeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlunnoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNome().map(f -> "nome=" + f + ", ").orElse("") +
            optionalCognome().map(f -> "cognome=" + f + ", ").orElse("") +
            optionalDataNascita().map(f -> "dataNascita=" + f + ", ").orElse("") +
            optionalCompitoInClasseId().map(f -> "compitoInClasseId=" + f + ", ").orElse("") +
            optionalClasseId().map(f -> "classeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
