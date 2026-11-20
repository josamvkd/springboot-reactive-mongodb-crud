package com.josamtechie.reactive.dao;

import com.josamtechie.reactive.dto.ProductDto;
import com.josamtechie.reactive.model.Product;
import org.springframework.data.domain.Range;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String>
{
    Flux<Product> findByPriceBetween(Range<Double> priceRange);
}
