package com.beer.project.repositories;

import com.beer.project.model.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {

    Optional<Beer> findByName(String name);

}
