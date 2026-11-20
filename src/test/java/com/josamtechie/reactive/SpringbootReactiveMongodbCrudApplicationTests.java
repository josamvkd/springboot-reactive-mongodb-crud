package com.josamtechie.reactive;

import com.josamtechie.reactive.controller.ProductController;
import com.josamtechie.reactive.dto.ProductDto;
import com.josamtechie.reactive.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
class SpringbootReactiveMongodbCrudApplicationTests
{

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductService service;

    @Test
    public void saveProductTest()
    {
//        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("1234", "Apple watch", 2, 40000));
//        when(service.saveProduct(productDtoMono)).thenReturn(productDtoMono);
        ProductDto dto = new ProductDto("1234", "Apple watch", 2, 40000);
        when(service.saveProduct(any())).thenReturn(Mono.just(dto));

        webTestClient.post().uri("/product/save")
                //.body(Mono.just(productDtoMono), ProductDto.class)
                .body(Mono.just(dto), ProductDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDto.class)
                .isEqualTo(dto);
    }

    @Test
    public void getAllProductsTest()
    {
//        Flux<ProductDto> productDtoFlux = Flux.just(new ProductDto("12345", "Apple watch pro", 2, 49000),
//                new ProductDto("45345", "Mac book pro", 2, 100000));
//        when(service.getAllProduct()).thenReturn(productDtoFlux);
        ProductDto watch = new ProductDto("12345", "Apple watch pro", 2, 49000);
        ProductDto mac = new ProductDto("45345", "Mac book pro", 2, 100000);
        when(service.getAllProduct()).thenReturn(Flux.just(watch, mac));

        Flux<ProductDto> responseBody = webTestClient.get().uri("/product/all")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(ProductDto.class)
                .getResponseBody();

//        StepVerifier.create(responseBody)
//                .expectSubscription()
//                .expectNext(new ProductDto("12345", "Apple watch pro", 2, 49000))
//                .expectNext(new ProductDto("45345", "Mac book pro", 2, 100000))
//                .verifyComplete();
        StepVerifier.create(responseBody)
                .expectNext(watch, mac)
                .expectComplete()
                .verify(Duration.ofSeconds(3));
        verify(service).getAllProduct();
    }

//    @Test
//    public void getProductByIdTest(){
//        Mono<ProductDto> productDtoMono=Mono.just(new ProductDto("102","mobile",1,10000));
//        when(service.getProductById(any())).thenReturn(productDtoMono);
//
//        Flux<ProductDto> responseBody = webTestClient.get().uri("/product/102")
//                .exchange()
//                .expectStatus().isOk()
//                .returnResult(ProductDto.class)
//                .getResponseBody();
//
//        StepVerifier.create(responseBody)
//                .expectSubscription()
//                .expectNextMatches(p->p.getName().equals("mobile"))
//                .verifyComplete();
//    }

    @Test
    void getProductByIdTest()
    {
        ProductDto mobile = new ProductDto("102", "mobile", 1, 10000);
        when(service.getProductById("102")).thenReturn(Mono.just(mobile));

        webTestClient.get().uri("/product/102")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ProductDto.class)
                .isEqualTo(mobile);

        verify(service).getProductById("102");
    }


//    @Test
//    public void updateProductTest(){
//        Mono<ProductDto> productDtoMono=Mono.just(new ProductDto("102","mobile",1,10000));
//        when(service.updateProduct(productDtoMono,"102")).thenReturn(productDtoMono);
//
//        webTestClient.put().uri("/product/update/102")
//                .body(Mono.just(productDtoMono),ProductDto.class)
//                .exchange()
//                .expectStatus().isOk();//200
//    }

    @Test
    void updateProductTest()
    {
        ProductDto dto = new ProductDto("102", "mobile", 1, 10000);
        when(service.updateProduct(any(), eq("102"))).thenReturn(Mono.just(dto));

        webTestClient.put().uri("/product/update/102")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ProductDto.class)
                .isEqualTo(dto);

        verify(service).updateProduct(any(), eq("102"));
    }

//    @Test
//    public void deleteProductTest(){
//        given(service.deleteProduct(any())).willReturn(Mono.empty());
//        webTestClient.delete().uri("/product/delete/102")
//                .exchange()
//                .expectStatus().isOk();//200
//    }

    @Test
    void deleteProductTest()
    {
        given(service.deleteProduct("102")).willReturn(Mono.empty());

        webTestClient.delete().uri("/product/delete/102")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(service).deleteProduct("102");
    }

}
