package com.beer.project.mapper;

import com.beer.project.model.Beer;
import com.beer.project.model.dtos.BeerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDTO dto);

    BeerDTO toDTO(Beer beer);

}
