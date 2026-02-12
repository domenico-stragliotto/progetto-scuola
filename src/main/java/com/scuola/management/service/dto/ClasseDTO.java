package com.scuola.management.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.scuola.management.domain.Classe} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClasseDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer numero;

    @NotNull
    @Size(max = 1)
    @Pattern(regexp = "[A-E]")
    private String sezione;

    @Size(max = 500)
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getSezione() {
        return sezione;
    }

    public void setSezione(String sezione) {
        this.sezione = sezione;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClasseDTO)) {
            return false;
        }

        ClasseDTO classeDTO = (ClasseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, classeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClasseDTO{" +
            "id=" + getId() +
            ", numero=" + getNumero() +
            ", sezione='" + getSezione() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
