package one.digitalinnovation.cigarrostock.mapper;

import one.digitalinnovation.cigarrostock.dto.CigarroDTO;
import one.digitalinnovation.cigarrostock.entity.Cigarro;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CigarroMapper {

    CigarroMapper INSTANCE = Mappers.getMapper(CigarroMapper.class);

    Cigarro toModel(CigarroDTO cigarroDTO);

    CigarroDTO toDTO(Cigarro cigarro);
}
