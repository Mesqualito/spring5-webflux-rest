package com.eigenbaumarkt.spring5webfluxrest.controllers;

import com.eigenbaumarkt.spring5webfluxrest.domain.Vendor;
import com.eigenbaumarkt.spring5webfluxrest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(VendorController.BASE_URL)
public class VendorController {

    public final static String BASE_URL = "/api/v1/vendors";
    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Flux<Vendor> listAllVendors() { return vendorRepository.findAll(); }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Vendor> listVendorById(@PathVariable String id) { return vendorRepository.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Void> createVendor(@RequestBody Publisher<Vendor> vendorStream) {
        return vendorRepository.saveAll(vendorStream).then();
    }

    @PutMapping("/{id}")
    Mono<Vendor> updateVendor(@PathVariable String id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @PatchMapping("/{id}")
    Mono<Vendor> patchVendor(@PathVariable String id, @RequestBody Vendor vendor) {

        // TODO: think about implementing a Service-Layer - better place logic there than here!
        // TODO: Keep the controller "clean" !!

        Vendor foundVendor = vendorRepository.findById(id).block();

        if( !foundVendor.getFirstName().equals(vendor.getFirstName()) ||
                !foundVendor.getLastName().equals(vendor.getLastName()) ) {

            if (!foundVendor.getFirstName().equals(vendor.getFirstName())) {
                foundVendor.setFirstName(vendor.getFirstName());
            }
            if (!foundVendor.getLastName().equals(vendor.getLastName())) {
                foundVendor.setLastName(vendor.getLastName());
            }
            vendorRepository.save(foundVendor);

        }

        return Mono.just(foundVendor);

    }

}
