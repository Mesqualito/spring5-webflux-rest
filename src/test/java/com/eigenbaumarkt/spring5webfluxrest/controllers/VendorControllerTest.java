package com.eigenbaumarkt.spring5webfluxrest.controllers;

import com.eigenbaumarkt.spring5webfluxrest.domain.Vendor;
import com.eigenbaumarkt.spring5webfluxrest.repositories.VendorRepository;
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
        given(vendorRepository.findAll())
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
        given(vendorRepository.findById("someId"))
                .willReturn(Mono.just(Vendor.builder().firstName("Jedini").lastName("Trgovac").build()));

        webTestClient.get()
                .uri(VendorController.BASE_URL + "/someId")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    void testCreateNewVendor() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

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

        given(vendorRepository.save(any(Vendor.class)))
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

    @Test
    void testPatchVendorWithChanges() {

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder()
                        .firstName("Malformed").lastName("Name").build()));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToPatch = Mono.just(Vendor.builder()
                .firstName("Klaus").lastName("Aftermath").build());

        webTestClient.patch()
                .uri(VendorController.BASE_URL + "/someId")
                .body(vendorMonoToPatch, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    void testPatchVendorWithoutChanges() {

        final String testFirstName = "Jack";
        final String testLastName = "Outbox";

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder()
                        .firstName(testFirstName)
                        .lastName(testLastName)
                        .build()));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToPatch = Mono.just(Vendor.builder()
                .firstName(testFirstName)
                .lastName(testLastName)
                .build());

        webTestClient.patch()
                .uri(VendorController.BASE_URL + "/someId")
                .body(vendorMonoToPatch, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

       verify(vendorRepository, never()).save(any());
    }

}
