package com.scuola.management.service.mapper;

import com.scuola.management.domain.Alunno;
import com.scuola.management.domain.Classe;
import com.scuola.management.service.dto.AlunnoDTO;
import com.scuola.management.service.dto.ClasseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Alunno} and its DTO {@link AlunnoDTO}.
 */
@Mapper(componentModel = "spring")
public interface AlunnoMapper extends EntityMapper<AlunnoDTO, Alunno> {
    @Mapping(target = "classe", source = "classe", qualifiedByName = "classeNumero")
    AlunnoDTO toDto(Alunno s);

    @Named("classeNumero")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "numero", source = "numero")
    @Mapping(target = "sezione", source = "sezione")
    ClasseDTO toDtoClasseNumeroSezione(Classe classe);
}
