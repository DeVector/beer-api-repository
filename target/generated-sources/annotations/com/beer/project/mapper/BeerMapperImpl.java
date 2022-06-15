package com.beer.project.mapper;

import com.beer.project.model.Beer;
import com.beer.project.model.dtos.BeerDTO;
import com.beer.project.model.dtos.BeerDTO.BeerDTOBuilder;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-06-15T12:15:56-0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.12 (Oracle Corporation)"
)
public class BeerMapperImpl implements BeerMapper {

    @Override
    public Beer toModel(BeerDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Beer beer = new Beer();

        beer.setId( dto.getId() );
        beer.setName( dto.getName() );
        beer.setBrand( dto.getBrand() );
        beer.setMax( dto.getMax() );
        beer.setQuantity( dto.getQuantity() );
        beer.setBeerType( dto.getBeerType() );

        return beer;
    }

    @Override
    public BeerDTO toDTO(Beer beer) {
        if ( beer == null ) {
            return null;
        }

        BeerDTOBuilder beerDTO = BeerDTO.builder();

        beerDTO.id( beer.getId() );
        beerDTO.name( beer.getName() );
        beerDTO.brand( beer.getBrand() );
        beerDTO.max( beer.getMax() );
        beerDTO.quantity( beer.getQuantity() );
        beerDTO.beerType( beer.getBeerType() );

        return beerDTO.build();
    }
}
