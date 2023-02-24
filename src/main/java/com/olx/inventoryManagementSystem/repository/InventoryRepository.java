package com.olx.inventoryManagementSystem.repository;

import com.olx.inventoryManagementSystem.controller.dto.SecondaryStatus;
import com.olx.inventoryManagementSystem.exceptions.InventoryNotFoundException;
import com.olx.inventoryManagementSystem.model.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository

public class InventoryRepository {
    JPAInventoryRepository jpaInventoryRepository;

    @Autowired
    public InventoryRepository(JPAInventoryRepository jpaInventoryRepository) {
        this.jpaInventoryRepository = jpaInventoryRepository;
    }

    public String createInventory(Inventory inventory) {
        Inventory savedInventory = this.jpaInventoryRepository.save(inventory);
        return savedInventory.getSku();
    }

    public Inventory findInventory(String sku) throws InventoryNotFoundException {
        Optional<Inventory> optionalCar = this.jpaInventoryRepository.findById(sku);
        if (optionalCar.isEmpty()) {
            throw new InventoryNotFoundException("Inventory not found for sku - " + sku);
        }
        return optionalCar.get();
    }

    public Page<Inventory> fetchInventories(Pageable pageable) {
        return this.jpaInventoryRepository.findAll(pageable);
    }

    public void updateStatus(String sku, SecondaryStatus statuses) {
        Optional<Inventory> inventories = this.jpaInventoryRepository.findById(sku);
        Inventory inventory = inventories.get();
        Inventory updatedInventory = changeStatus(inventory, statuses);
        this.jpaInventoryRepository.save(inventory);
    }

    public void addStatus(String sku, SecondaryStatus statuses) {
        Optional<Inventory> inventories = this.jpaInventoryRepository.findById(sku);
        Inventory inventory = inventories.get();
        ArrayList<SecondaryStatus> statusArrayList = inventory.getSecondaryStatus();
        statusArrayList.add(statuses);
        this.jpaInventoryRepository.save(inventory);
    }

    public Inventory changeStatus(Inventory inventory, SecondaryStatus statuses) {
        ArrayList<SecondaryStatus> statusArrayList = inventory.getSecondaryStatus();
        for (SecondaryStatus status : statusArrayList) {
            if (status.getName().equals(statuses.getName())) {
                status.setStatus(statuses.getStatus());
            }
        }
        inventory.setSecondaryStatus(statusArrayList);
        return inventory;
    }
}
