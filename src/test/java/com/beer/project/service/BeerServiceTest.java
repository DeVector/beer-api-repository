package com.beer.project.service;

import com.beer.project.builder.BeerDTOBuilder;
import com.beer.project.exception.BeerAlreadyRegisteredException;
import com.beer.project.mapper.BeerMapper;
import com.beer.project.model.Beer;
import com.beer.project.model.dtos.BeerDTO;
import com.beer.project.repositories.BeerRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    @Mock
    private BeerRepository repository;

    private BeerMapper mapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService service;

    @Test
    void whenBeerInformedThenItShuldBeCreated() throws BeerAlreadyRegisteredException {

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedSavedBeer = mapper.toModel(beerDTO);

        when(repository.findByName(beerDTO.getName())).thenReturn(Optional.empty());
        when(repository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        BeerDTO createdBeerDTO = service.createBeer(beerDTO);

        assertThat(createdBeerDTO.getId(), is(equalTo(beerDTO.getId())));
        assertThat(createdBeerDTO.getName(), is(equalTo(beerDTO.getName())));
        assertThat(createdBeerDTO.getQuantity(), is(equalTo(beerDTO.getQuantity())));

    }

    @Test
    void whenAlreadyRegisterBeerInformedThenExceptionShouldBeThrow() {

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicateBeer = mapper.toModel(beerDTO);

        when(repository.findByName(beerDTO.getName())).thenReturn(Optional.of(duplicateBeer));

        assertThrows(BeerAlreadyRegisteredException.class, () -> service.createBeer(beerDTO));

    }

}
