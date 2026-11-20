package com.josamtechie.reactive.controller;

import com.josamtechie.reactive.dto.ProductDto;
import com.josamtechie.reactive.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController
{
    private final ProductService service;

    @GetMapping("/all")
    public Flux<ProductDto> getAllProducts()
    {
        return service.getAllProduct();
    }

    @GetMapping("/{id}")
    public Mono<ProductDto> getProductById(@PathVariable String id)
    {
        return service.getProductById(id);
    }

    @GetMapping("/price-range")
    public Flux<ProductDto> productsPriceRange(@RequestParam("min") double min, @RequestParam("max") double max)
    {
        return service.productPriceRange(min, max);
    }

    @PostMapping("/save")
    public Mono<ProductDto> saveProduct(@RequestBody Mono<ProductDto> productDtoMono)
    {
        return service.saveProduct(productDtoMono);
    }

    @PutMapping("/update/{id}")
    public Mono<ProductDto> updateProduct(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable String id)
    {
        return service.updateProduct(productDtoMono, id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> deleteProduct(@PathVariable String id)
    {
        return service.deleteProduct(id);
    }
}
