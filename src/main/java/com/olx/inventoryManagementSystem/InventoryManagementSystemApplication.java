package com.olx.inventoryManagementSystem;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(name = "JWT", paramName = "Authorization", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class InventoryManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementSystemApplication.class, args);
    }

}
