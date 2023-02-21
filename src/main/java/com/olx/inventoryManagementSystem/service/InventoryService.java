package com.olx.inventoryManagementSystem.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.olx.inventoryManagementSystem.controller.dto.InventoryRequest;
import com.olx.inventoryManagementSystem.model.Inventory;
import com.olx.inventoryManagementSystem.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InventoryService {

    InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    public String createInventory(InventoryRequest inventoryRequest)  {
        UUID sku = UUID.randomUUID();
        String uuidAsString = sku.toString();
        Gson g=new Gson();
        String attributes=g.toJson(inventoryRequest.getAttributes());
        String secondaryStatus=g.toJson(inventoryRequest.getSecondaryStatus());
        Inventory inventory = new Inventory(sku.toString(),inventoryRequest.getType(),inventoryRequest.getLocation(),LocalDateTime.now(),LocalDateTime.now(),"user","user",attributes,inventoryRequest.getCostPrice(),secondaryStatus );
        return this.inventoryRepository.createInventory(inventory);
    }
}
