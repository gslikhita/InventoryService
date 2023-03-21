package com.olx.inventoryManagementSystem.repository;

import com.olx.inventoryManagementSystem.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPAInventoryRepository extends JpaRepository<Inventory, Integer> {

    Optional<Inventory> findBySku(String sku);

}
