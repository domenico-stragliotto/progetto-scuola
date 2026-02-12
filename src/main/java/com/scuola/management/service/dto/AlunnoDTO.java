package com.scuola.management.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.scuola.management.domain.Alunno} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlunnoDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String nome;

    @NotNull
    @Size(max = 100)
    private String cognome;

    @NotNull
    private LocalDate dataNascita;

    @NotNull
    private ClasseDTO classe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public ClasseDTO getClasse() {
        return classe;
    }

    public void setClasse(ClasseDTO classe) {
        this.classe = classe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlunnoDTO)) {
            return false;
        }

        AlunnoDTO alunnoDTO = (AlunnoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, alunnoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlunnoDTO{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", cognome='" + getCognome() + "'" +
            ", dataNascita='" + getDataNascita() + "'" +
            ", classe=" + getClasse() +
            "}";
    }
}
