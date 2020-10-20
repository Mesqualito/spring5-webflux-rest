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
            loadCategories();
        }

        if (vendorRepository.count().block() == 0) {
            loadVendors();
        }

    }

    private void loadCategories() {

        Category monster = new Category();
        monster.setDescription("čudovište");

        Category animal = new Category();
        animal.setDescription("životinja");

        categoryRepository.save(monster).block();
        categoryRepository.save(animal).block();

        log.info("Data inject: " + categoryRepository.count().block() + " categories saved.");

    }

    private void loadVendors() {

        Vendor vendor1 = new Vendor();
        vendor1.setFirstName("Prvi");
        vendor1.setLastName("Prodavač");

        Vendor vendor2 = new Vendor();
        vendor2.setFirstName("Drugi");
        vendor2.setLastName("Prodavač");

        vendorRepository.save(vendor1).block();
        vendorRepository.save(vendor2).block();

        log.info("Data inject: " + vendorRepository.count().block() + " vendors saved.");

    }

}
