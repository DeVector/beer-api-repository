package com.beer.project.service;

import com.beer.project.builder.BeerDTOBuilder;
import com.beer.project.exception.BeerAlreadyRegisteredException;
import com.beer.project.exception.BeerNotFoundException;
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

import java.util.Collections;
import java.util.List;
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
        //Validar a cerveja para a criação

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
        //Validar o throws quando a cerveja ja existe

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicateBeer = mapper.toModel(beerDTO);

        when(repository.findByName(beerDTO.getName())).thenReturn(Optional.of(duplicateBeer));

        assertThrows(BeerAlreadyRegisteredException.class, () -> service.createBeer(beerDTO));

    }

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        //Validar para encontrar uma cerveja pelo nome

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer foundBeer = mapper.toModel(beerDTO);

        when(repository.findByName(foundBeer.getName())).thenReturn(Optional.of(foundBeer));

        BeerDTO foundBeerDTO = service.findByName(beerDTO.getName());

        assertThat(foundBeerDTO, is(equalTo(beerDTO)));

    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() throws BeerNotFoundException {
        //Validatar quando não encontra nenhuma cerveja no db pelo nome

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(repository.findByName(beerDTO.getName())).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> service.findByName(beerDTO.getName()));

    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfBeers() {

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = mapper.toModel(beerDTO);

        when(repository.findAll()).thenReturn(Collections.singletonList(expectedBeer));

        List<BeerDTO> foundBeerDTO = service.findAll();

        assertThat(foundBeerDTO, is(not(empty())));
        assertThat(foundBeerDTO.get(0), is(equalTo(beerDTO)));

    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyList() {

        when(repository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<BeerDTO> foundBeerDTO = service.findAll();

        assertThat(foundBeerDTO, is(empty()));

    }
}
