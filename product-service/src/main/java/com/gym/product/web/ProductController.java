package com.gym.product.web;

import com.gym.product.service.ProductService;
import com.gym.product.web.dto.CreateProductRequest;
import com.gym.product.web.dto.ProductResponse;
import com.gym.product.web.dto.UpdateProductRequest;
import com.gym.product.web.dto.UpdateProductStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Mono<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{productId}")
    public Mono<ProductResponse> update(@PathVariable("productId") UUID productId,
                                        @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(productId, request);
    }

    @PatchMapping("/{productId}/status")
    public Mono<ProductResponse> updateStatus(@PathVariable("productId") UUID productId,
                                              @Valid @RequestBody UpdateProductStatusRequest request) {
        return productService.updateStatus(productId, request);
    }

    @GetMapping("/{productId}")
    public Mono<ProductResponse> findById(@PathVariable("productId") UUID productId) {
        return productService.findById(productId);
    }

    @GetMapping
    public Flux<ProductResponse> findAll() {
        return productService.findAll();
    }
}
