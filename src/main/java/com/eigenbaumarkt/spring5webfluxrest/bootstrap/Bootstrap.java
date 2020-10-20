package com.eigenbaumarkt.spring5webfluxrest.bootstrap;

import com.eigenbaumarkt.spring5webfluxrest.domain.Category;
import com.eigenbaumarkt.spring5webfluxrest.domain.Vendor;
import com.eigenbaumarkt.spring5webfluxrest.repositories.CategoryRepository;
import com.eigenbaumarkt.spring5webfluxrest.repositories.VendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private CategoryRepository categoryRepository;
    private VendorRepository vendorRepository;

    public Bootstrap(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
    }


    @Override
    public void run(String... args) throws Exception {

        if (categoryRepository.count().block() == 0) {
            categoryRepository.save(Category
                    .builder()
                    .description("čudovište") // monster
                    .build())
                    .block();
            categoryRepository.save(Category
                    .builder()
                    .description("životinja") // animal
                    .build())
                    .block();

            log.info("Data inject: " + categoryRepository.count().block() + " categories saved.");
        }

        if (vendorRepository.count().block() == 0) {
            vendorRepository.save(Vendor
                    .builder()
                    .firstName("Prvi")
                    .lastName("Prodavač")
                    .build())
                    .block();
            vendorRepository.save(Vendor
                    .builder()
                    .firstName("Drugi")
                    .lastName("Prodavač")
                    .build())
                    .block();

            log.info("Data inject: " + vendorRepository.count().block() + " vendors saved.");
        }

    }
}
