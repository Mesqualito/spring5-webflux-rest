package com.eigenbaumarkt.spring5webfluxrest.controllers;

import com.eigenbaumarkt.spring5webfluxrest.domain.Category;
import com.eigenbaumarkt.spring5webfluxrest.domain.Vendor;
import com.eigenbaumarkt.spring5webfluxrest.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

class VendorControllerTest {

    WebTestClient webTestClient;
    VendorRepository vendorRepository;
    VendorController vendorController;

    @BeforeEach
    void setUp() throws Exception {
        // simulating the DJ in Unit-Tests, which is done by the Spring framework on application runtime
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    void listAllVendors() {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Prvi").lastName("Prodavač").build(),
                        Vendor.builder().firstName("Drugi").lastName("Prodavač").build()));

        webTestClient.get()
                .uri(VendorController.BASE_URL)
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    void listVendorById() {
        BDDMockito.given(vendorRepository.findById("someId"))
                .willReturn(Mono.just(Vendor.builder().firstName("Jedini").lastName("Trgovac").build()));

        webTestClient.get()
                .uri(VendorController.BASE_URL + "/someId")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    void testCreateNewVendor() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().build()));

        Mono<Vendor> vendorMonoToSave = Mono.just(Vendor.builder()
        .firstName("Jedini").lastName("Trgovac").build());

        webTestClient.post()
                .uri(VendorController.BASE_URL)
                .body(vendorMonoToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void testUpdateVendor() {

        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        // Test Mono<Vendor> to pass into WebTestClient
        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder()
        .firstName("Jedini").lastName("Trgovac").build());

        webTestClient.put()
                .uri(VendorController.BASE_URL + "/someId")
                .body(vendorMonoToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
