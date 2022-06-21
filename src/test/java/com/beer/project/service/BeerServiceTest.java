package com.beer.project.service;

import com.beer.project.builder.BeerDTOBuilder;
import com.beer.project.exception.BeerAlreadyRegisteredException;
import com.beer.project.exception.BeerNotFoundException;
import com.beer.project.exception.BeerStockExceededException;
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

    private static final long INVALID_BEER_ID = 1L;

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

    @Test
    void whenExclusionIdValidBeerShouldDelete() throws BeerNotFoundException{

        BeerDTO deleteBeerDto = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer deleteBeer = mapper.toModel(deleteBeerDto);

        when(repository.findById(deleteBeerDto.getId())).thenReturn(Optional.of(deleteBeer));
        doNothing().when(repository).deleteById(deleteBeerDto.getId());

        service.deleteById(deleteBeerDto.getId());

        verify(repository, times(1)).findById(deleteBeerDto.getId());
        verify(repository, times(1)).deleteById(deleteBeerDto.getId());

    }

    @Test
    void whenExclusionIsCalledWithInvalidIdThenExceptionShouldBeThrown() {

        when(repository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> service.deleteById(INVALID_BEER_ID));
    }

    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beer = mapper.toModel(beerDTO);

        when(repository.findById(beerDTO.getId())).thenReturn(Optional.of(beer));
        when(repository.save(beer)).thenReturn(beer);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = beerDTO.getQuantity() + quantityToIncrement;
        BeerDTO incrementToBeerDTO = service.increment(beerDTO.getId(), quantityToIncrement);

        assertThat(incrementToBeerDTO.getQuantity(), is(equalTo(expectedQuantityAfterIncrement)));
        assertThat(beerDTO.getMax(), is(greaterThan(expectedQuantityAfterIncrement)));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beer = mapper.toModel(beerDTO);

        when(repository.findById(beerDTO.getId())).thenReturn(Optional.of(beer));

        int quantityToIncrement = 80;
        assertThrows(BeerStockExceededException.class, () -> service.increment(beerDTO.getId(), quantityToIncrement));

    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {

        int quantityToIncrement = 10;

        when(repository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> service.increment(INVALID_BEER_ID, quantityToIncrement));

    }

    @Test
    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beer = mapper.toModel(beerDTO);

        when(repository.findById(beerDTO.getId())).thenReturn(Optional.of(beer));
        when(repository.save(beer)).thenReturn(beer);

        int quantityToDecrement = 5;
        int quantityAfterDecrement = beerDTO.getQuantity() - quantityToDecrement;
        BeerDTO decrementBeerDTO = service.decrement(beerDTO.getId(), quantityToDecrement);

        assertThat(decrementBeerDTO.getQuantity(), is(equalTo(quantityAfterDecrement)));
        assertThat(quantityAfterDecrement, is(greaterThan(0)));

    }

    @Test
    void whenDecrementIsCalledToEmptyStockThenEmptyBeerStock() throws BeerNotFoundException, BeerStockExceededException {

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beer = mapper.toModel(beerDTO);

        when(repository.findById(beerDTO.getId())).thenReturn(Optional.of(beer));
        when(repository.save(beer)).thenReturn(beer);

        int quantityDecrement = 10;
        int quantityAfterDecrement = beerDTO.getQuantity() - quantityDecrement;
        BeerDTO decrementBeerDTO = service.decrement(beerDTO.getId(), quantityDecrement);

        assertThat(quantityAfterDecrement, is(equalTo(0)));
        assertThat(quantityAfterDecrement, is(equalTo(decrementBeerDTO.getQuantity())));

    }

    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer beer = mapper.toModel(beerDTO);

        when(repository.findById(beerDTO.getId())).thenReturn(Optional.of(beer));

        int quantityToDecrement = 80;
        assertThrows(BeerStockExceededException.class, () -> service.decrement(beerDTO.getId(), quantityToDecrement));

    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {

        int quantityDecremet = 10;

        when(repository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> service.decrement(INVALID_BEER_ID, quantityDecremet));

    }

}