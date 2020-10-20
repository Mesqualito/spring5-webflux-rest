package com.eigenbaumarkt.spring5webfluxrest.controllers;

import com.eigenbaumarkt.spring5webfluxrest.domain.Vendor;
import com.eigenbaumarkt.spring5webfluxrest.repositories.VendorRepository;
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
}
