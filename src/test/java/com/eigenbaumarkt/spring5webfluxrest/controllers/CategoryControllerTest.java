package com.eigenbaumarkt.spring5webfluxrest.controllers;

import com.eigenbaumarkt.spring5webfluxrest.domain.Category;
import com.eigenbaumarkt.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


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
        given(categoryRepository.findAll())
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
        given(categoryRepository.findById("someId"))
                .willReturn(Mono.just(Category.builder().description("Test-Category").build()));

        webTestClient.get()
                .uri(CategoryController.BASE_URL + "/someId")
                .exchange()
                .expectBody(Category.class);
    }

    @Test
    void testCreateNewCategory() {
        given(categoryRepository.saveAll(any(Publisher.class)))
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
        given(categoryRepository.save(any(Category.class)))
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

    @Test
    void testPatchCategoryWithChanges() {

        // to prevent a NPE with 'Category foundCategory = categoryRepository.findById(id).block();'
        // in the PATCH-method 'patchCategory(...)' in the CategoryController, we have to serve a Mock:
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().build()));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().description("Not really a description!").build()));

        // we do change the description in the WebTestClient, here is our patch:
        Mono<Category> categoryMonoToPatch = Mono.just(Category.builder()
        .description("A category patch").build());

        webTestClient.patch()
                .uri(CategoryController.BASE_URL + "/someId")
                .body(categoryMonoToPatch, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        // verify that the save-method has been run at least once:
        verify(categoryRepository).save(any());

    }

    @Test
    void testPatchCategoryNoChanges() {

        final String testString = "This will stay the same like the id!";

        // to prevent a NPE with 'Category foundCategory = categoryRepository.findById(id).block();'
        // in the PATCH-method 'patchCategory(...)' in the CategoryController, we have to serve a Mock:
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().description(testString).build()));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder()
                        .description(testString).build()));

        // we don't change anything really...
        Mono<Category> categoryMonoToPatch = Mono.just(Category.builder()
                .description(testString).build());

        webTestClient.patch()
                .uri(CategoryController.BASE_URL + "/someId")
                .body(categoryMonoToPatch, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        /*
        Assertions.assertThrows(NumberFormatException.class, () -> {
            verify(categoryRepository).save(any());
        });
        */
        verify(categoryRepository, never()).save(any());

    }

}
