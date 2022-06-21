package com.beer.project.service;

import com.beer.project.exception.BeerAlreadyRegisteredException;
import com.beer.project.exception.BeerNotFoundException;
import com.beer.project.exception.BeerStockExceededException;
import com.beer.project.mapper.BeerMapper;
import com.beer.project.model.Beer;
import com.beer.project.model.dtos.BeerDTO;
import com.beer.project.repositories.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

    private final BeerRepository repository;

    private final BeerMapper mapper = BeerMapper.INSTANCE;

    public BeerDTO createBeer(BeerDTO dto) throws BeerAlreadyRegisteredException {

        verifyIfExistsRegistered(dto.getName());
        Beer beer = mapper.toModel(dto);
        Beer saved = repository.save(beer);
        return mapper.toDTO(saved);

    }

    public BeerDTO findByName(String name) throws BeerNotFoundException {
        Beer beer = repository.findByName(name)
                .orElseThrow(() -> new BeerNotFoundException(name));
        return mapper.toDTO(beer);
    }

    public List<BeerDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BeerNotFoundException {
        verifyIfExists(id);
        repository.deleteById(id);
    }

    private void verifyIfExistsRegistered(String name) throws BeerAlreadyRegisteredException {

        Optional<Beer> optional = repository.findByName(name);
        if (optional.isPresent()) {
            throw new BeerAlreadyRegisteredException(name);
        }

    }

    private Beer verifyIfExists(Long id) throws BeerNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new BeerNotFoundException(id));
    }

    public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExceededException {
        Beer beerToIncrement = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + beerToIncrement.getQuantity();
        if (quantityAfterIncrement <= beerToIncrement.getMax()) {
            beerToIncrement.setQuantity(beerToIncrement.getQuantity() + quantityToIncrement);
            Beer incrementBeer = repository.save(beerToIncrement);
            return mapper.toDTO(incrementBeer);
        }
        throw new BeerStockExceededException(id, quantityToIncrement);
    }

    public BeerDTO decrement(Long id, int quantityToDecrement) throws BeerNotFoundException, BeerStockExceededException {
        Beer beerToDecrementStock = verifyIfExists(id);
        int beerStockAfterDecremented = beerToDecrementStock.getQuantity() - quantityToDecrement;
        if (beerStockAfterDecremented >= 0) {
            beerToDecrementStock.setQuantity(beerStockAfterDecremented);
            Beer decrementedBeerStock = repository.save(beerToDecrementStock);
            return mapper.toDTO(decrementedBeerStock);
        }
        throw new BeerStockExceededException(id, quantityToDecrement);
    }
}
