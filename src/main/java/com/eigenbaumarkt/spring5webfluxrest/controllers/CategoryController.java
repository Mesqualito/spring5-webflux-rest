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
        // @RequestBody: Spring will examine the PUTted json-Object, parse a Category-object out of it
        // and reach it as parameter to the updateCategory-method
        category.setId(id);
        return categoryRepository.save(category);
    }

    @PatchMapping("/{id}")
    Mono<Category> patchCategory(@PathVariable String id, @RequestBody Category category) {

        // TODO: think about implementing a Service-Layer - better place logic there than here!
        // TODO: Keep the controller "clean" !!

        Category foundCategory = categoryRepository.findById(id).block();

        if (!foundCategory.getDescription().equals(category.getDescription())) {

            foundCategory.setDescription(category.getDescription());
            return categoryRepository.save(foundCategory);

        }

        return Mono.just(foundCategory);

    }


}
