package com.beer.project.controller;

import com.beer.project.exception.BeerAlreadyRegisteredException;
import com.beer.project.model.dtos.BeerDTO;
import com.beer.project.service.BeerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController {

    private final BeerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerDTO createBeer(@RequestBody @Valid BeerDTO dto) throws BeerAlreadyRegisteredException {
        return service.createBeer(dto);
    }

}
