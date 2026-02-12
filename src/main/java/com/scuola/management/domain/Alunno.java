package com.scuola.management.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Alunno.
 */
@Entity
@Table(name = "alunno")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Alunno implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @NotNull
    @Size(max = 100)
    @Column(name = "cognome", length = 100, nullable = false)
    private String cognome;

    @NotNull
    @Column(name = "data_nascita", nullable = false)
    private LocalDate dataNascita;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "alunno")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "alunno" }, allowSetters = true)
    private Set<CompitoInClasse> compitoInClasses = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private Classe classe;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Alunno id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Alunno nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return this.cognome;
    }

    public Alunno cognome(String cognome) {
        this.setCognome(cognome);
        return this;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public LocalDate getDataNascita() {
        return this.dataNascita;
    }

    public Alunno dataNascita(LocalDate dataNascita) {
        this.setDataNascita(dataNascita);
        return this;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public Set<CompitoInClasse> getCompitoInClasses() {
        return this.compitoInClasses;
    }

    public void setCompitoInClasses(Set<CompitoInClasse> compitoInClasses) {
        if (this.compitoInClasses != null) {
            this.compitoInClasses.forEach(i -> i.setAlunno(null));
        }
        if (compitoInClasses != null) {
            compitoInClasses.forEach(i -> i.setAlunno(this));
        }
        this.compitoInClasses = compitoInClasses;
    }

    public Alunno compitoInClasses(Set<CompitoInClasse> compitoInClasses) {
        this.setCompitoInClasses(compitoInClasses);
        return this;
    }

    public Alunno addCompitoInClasse(CompitoInClasse compitoInClasse) {
        this.compitoInClasses.add(compitoInClasse);
        compitoInClasse.setAlunno(this);
        return this;
    }

    public Alunno removeCompitoInClasse(CompitoInClasse compitoInClasse) {
        this.compitoInClasses.remove(compitoInClasse);
        compitoInClasse.setAlunno(null);
        return this;
    }

    public Classe getClasse() {
        return this.classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public Alunno classe(Classe classe) {
        this.setClasse(classe);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Alunno)) {
            return false;
        }
        return getId() != null && getId().equals(((Alunno) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Alunno{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", cognome='" + getCognome() + "'" +
            ", dataNascita='" + getDataNascita() + "'" +
            "}";
    }
}
