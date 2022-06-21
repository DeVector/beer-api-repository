package com.beer.project.controller;

import com.beer.project.exception.BeerAlreadyRegisteredException;
import com.beer.project.exception.BeerNotFoundException;
import com.beer.project.exception.BeerStockExceededException;
import com.beer.project.model.dtos.BeerDTO;
import com.beer.project.model.dtos.QuantityDTO;
import com.beer.project.service.BeerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/{name}")
    public BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException {
        return service.findByName(name);
    }

    @GetMapping
    public List<BeerDTO> findAll(){
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws BeerNotFoundException {
        service.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public BeerDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO)
            throws BeerNotFoundException, BeerStockExceededException {
        return service.increment(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public BeerDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO)
            throws BeerNotFoundException, BeerStockExceededException {
        return service.decrement(id, quantityDTO.getQuantity());
    }

}
