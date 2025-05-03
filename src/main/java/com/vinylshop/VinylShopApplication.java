// Починаємо з основних класів проєкту

package com.vinylshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Головний клас запуску програми Vinyl Shop
 */
@SpringBootApplication
public class VinylShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(VinylShopApplication.class, args);
    }
}

