package com.eigenbaumarkt.spring5webfluxrest.controllers;

import com.eigenbaumarkt.spring5webfluxrest.domain.Category;
import com.eigenbaumarkt.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;


class CategoryControllerTest {

    WebTestClient webTestClient;
    CategoryRepository categoryRepository;
    CategoryController categoryController;

    @BeforeEach
    void setUp() throws Exception {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    void testListAllCategories() {
        // Behaviour-Driven Mockito
        BDDMockito.given(categoryRepository.findAll())
                .willReturn(Flux.just(Category.builder().description("Test-Category 1").build(),
                        Category.builder().description("Test-Category 2").build()));

        webTestClient.get()
                .uri(CategoryController.BASE_URL)
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    void testListCategoryById() {
        BDDMockito.given(categoryRepository.findById("someId"))
                .willReturn(Mono.just(Category.builder().description("Test-Category").build()));

        webTestClient.get()
                .uri(CategoryController.BASE_URL + "/someId")
                .exchange()
                .expectBody(Category.class);
    }

    @Test
    void testCreateNewCategory() {
        BDDMockito.given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().build()));

        Mono<Category> categoryMonoToSave = Mono.just(Category.builder()
                .description("A new category is born every minute").build());

        webTestClient.post()
                .uri(CategoryController.BASE_URL)
                .body(categoryMonoToSave, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();

    }

    @Test
    void testUpdateCategory() {

        // setting up the Mockito Mock to take in any Category-Object
        // and return back a Mono of an empty Category-Object
        BDDMockito.given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        // setting up an Test-Object which will be passed in
        Mono<Category> categoryMonoToUpdate = Mono.just(Category.builder()
                .description("An updated category").build());

        // testing the Mockito Mock with the Test-Object parsed into json through the WebTestClient
        // and checking the return of the Controller-Method
        webTestClient.put()
                .uri(CategoryController.BASE_URL + "/someId")
                .body(categoryMonoToUpdate, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

    }

}
