package com.olx.inventoryManagementSystem.repository;

import com.olx.inventoryManagementSystem.exceptions.InventoryNotFoundException;
import com.olx.inventoryManagementSystem.model.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public class InventoryRepository {
    public static final String INVENTORY_NOT_FOUND = "Inventory not found for sku - ";
    JPAInventoryRepository jpaInventoryRepository;

    @Autowired
    public InventoryRepository(JPAInventoryRepository jpaInventoryRepository) {
        this.jpaInventoryRepository = jpaInventoryRepository;
    }

    public String create(Inventory inventory) {
        return this.jpaInventoryRepository.save(inventory).getSku();
    }

    public Inventory find(String sku) throws InventoryNotFoundException {
        Optional<Inventory> optionalInventory = this.jpaInventoryRepository.findBySku(sku);
        if (optionalInventory.isEmpty()) {
            throw new InventoryNotFoundException(INVENTORY_NOT_FOUND + sku);
        }
        return optionalInventory.get();
    }

    public Page<Inventory> fetch(Pageable pageable) {
        return this.jpaInventoryRepository.findAll(pageable);
    }

    public void save(Inventory inventory) {
        this.jpaInventoryRepository.save(inventory);
    }
}
