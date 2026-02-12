package com.scuola.management.service.mapper;

import com.scuola.management.domain.Alunno;
import com.scuola.management.domain.CompitoInClasse;
import com.scuola.management.service.dto.AlunnoDTO;
import com.scuola.management.service.dto.CompitoInClasseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CompitoInClasse} and its DTO {@link CompitoInClasseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompitoInClasseMapper extends EntityMapper<CompitoInClasseDTO, CompitoInClasse> {
    @Mapping(target = "alunno", source = "alunno", qualifiedByName = "alunnoId")
    CompitoInClasseDTO toDto(CompitoInClasse s);

    @Named("alunnoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AlunnoDTO toDtoAlunnoId(Alunno alunno);
}
