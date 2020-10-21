package com.eigenbaumarkt.spring5webfluxrest.controllers;

import com.eigenbaumarkt.spring5webfluxrest.domain.Category;
import com.eigenbaumarkt.spring5webfluxrest.repositories.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(CategoryController.BASE_URL)
public class CategoryController {

    public final static String BASE_URL = "/api/v1/categories";
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    Flux<Category> listAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    Mono<Category> listCategoryById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }

    // create-Method doesn't return a Body, but a Http status (201)
    // also any reactive type (Mono or Flux) can be passed in
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Void> createCategory(@RequestBody Publisher<Category> categoryStream) {
        return categoryRepository.saveAll(categoryStream).then();
    }

    @PutMapping("/{id}")
    // @ResponseStatus(HttpStatus.OK) -- default !
    Mono<Category> updateCategory(@PathVariable String id, @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }


}
