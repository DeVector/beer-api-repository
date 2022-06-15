package com.beer.project.service;

import com.beer.project.exception.BeerAlreadyRegisteredException;
import com.beer.project.exception.BeerNotFoundException;
import com.beer.project.mapper.BeerMapper;
import com.beer.project.model.Beer;
import com.beer.project.model.dtos.BeerDTO;
import com.beer.project.repositories.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    private void verifyIfExistsRegistered(String name) throws BeerAlreadyRegisteredException {

        Optional<Beer> optional = repository.findByName(name);
        if (optional.isPresent()) {
            throw new BeerAlreadyRegisteredException(name);
        }

    }
}
