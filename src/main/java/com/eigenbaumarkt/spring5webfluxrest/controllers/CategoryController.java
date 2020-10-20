package com.eigenbaumarkt.spring5webfluxrest.controllers;

import com.eigenbaumarkt.spring5webfluxrest.domain.Category;
import com.eigenbaumarkt.spring5webfluxrest.repositories.CategoryRepository;
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
    @ResponseStatus(HttpStatus.OK)
    Flux<Category> listAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Category> listCategoryById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }



}
