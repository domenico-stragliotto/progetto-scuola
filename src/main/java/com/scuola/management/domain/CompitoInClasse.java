package com.scuola.management.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scuola.management.domain.enumeration.Materia;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CompitoInClasse.
 */
@Entity
@Table(name = "compito_in_classe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompitoInClasse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "data", nullable = false)
    private LocalDate data;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "materia", nullable = false)
    private Materia materia;

    @NotNull
    @DecimalMin(value = "1")
    @DecimalMax(value = "10")
    @Column(name = "risultato", nullable = false)
    private Double risultato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "compitoInClasses", "classe" }, allowSetters = true)
    private Alunno alunno;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CompitoInClasse id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return this.data;
    }

    public CompitoInClasse data(LocalDate data) {
        this.setData(data);
        return this;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Materia getMateria() {
        return this.materia;
    }

    public CompitoInClasse materia(Materia materia) {
        this.setMateria(materia);
        return this;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Double getRisultato() {
        return this.risultato;
    }

    public CompitoInClasse risultato(Double risultato) {
        this.setRisultato(risultato);
        return this;
    }

    public void setRisultato(Double risultato) {
        this.risultato = risultato;
    }

    public Alunno getAlunno() {
        return this.alunno;
    }

    public void setAlunno(Alunno alunno) {
        this.alunno = alunno;
    }

    public CompitoInClasse alunno(Alunno alunno) {
        this.setAlunno(alunno);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompitoInClasse)) {
            return false;
        }
        return getId() != null && getId().equals(((CompitoInClasse) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompitoInClasse{" +
            "id=" + getId() +
            ", data='" + getData() + "'" +
            ", materia='" + getMateria() + "'" +
            ", risultato=" + getRisultato() +
            "}";
    }
}
