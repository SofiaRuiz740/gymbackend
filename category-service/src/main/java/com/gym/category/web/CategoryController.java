package com.gym.category.web;

import com.gym.category.service.CategoryService;
import com.gym.category.web.dto.CategoryResponse;
import com.gym.category.web.dto.CreateCategoryRequest;
import com.gym.category.web.dto.UpdateCategoryRequest;
import com.gym.category.web.dto.UpdateCategoryStatusRequest;
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
@RequestMapping(path = "/api/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Mono<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        return categoryService.create(request);
    }

    @PutMapping("/{categoryId}")
    public Mono<CategoryResponse> update(@PathVariable("categoryId") UUID categoryId,
                                         @Valid @RequestBody UpdateCategoryRequest request) {
        return categoryService.update(categoryId, request);
    }

    @PatchMapping("/{categoryId}/status")
    public Mono<CategoryResponse> updateStatus(@PathVariable("categoryId") UUID categoryId,
                                               @Valid @RequestBody UpdateCategoryStatusRequest request) {
        return categoryService.updateStatus(categoryId, request);
    }

    @GetMapping("/{categoryId}")
    public Mono<CategoryResponse> findById(@PathVariable("categoryId") UUID categoryId) {
        return categoryService.findById(categoryId);
    }

    @GetMapping
    public Flux<CategoryResponse> findAll() {
        return categoryService.findAll();
    }
}
