package com.olx.inventoryManagementSystem.controller;

import com.olx.inventoryManagementSystem.controller.dto.CreateInventoryResponse;
import com.olx.inventoryManagementSystem.controller.dto.InventoryRequest;
import com.olx.inventoryManagementSystem.controller.dto.InventoryResponse;
import com.olx.inventoryManagementSystem.controller.dto.SecondaryStatus;
import com.olx.inventoryManagementSystem.exceptions.InvalidTypeException;
import com.olx.inventoryManagementSystem.exceptions.InventoryNotFoundException;
import com.olx.inventoryManagementSystem.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class InventoryController {

    InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/inventories")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateInventoryResponse createInventory(@Valid @RequestBody InventoryRequest inventoryRequest) throws InvalidTypeException {
        String inventoryId = this.inventoryService.createInventory(inventoryRequest);
        return new CreateInventoryResponse(inventoryId);
    }

    @GetMapping("/inventories/{sku}")
    public InventoryResponse getInventory(@PathVariable String sku) throws InventoryNotFoundException {
        return this.inventoryService.getInventory(sku);
    }

    @GetMapping("/inventories")
    public List<InventoryResponse> getInventories(@PageableDefault(sort = "sku", direction = Sort.Direction.ASC) Pageable pageable) {
        return this.inventoryService.getInventories(pageable);
    }

    @PutMapping("/inventories/{sku}/statuses")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable String sku, @RequestBody ArrayList<SecondaryStatus> secondaryStatuses) throws InventoryNotFoundException {
        this.inventoryService.updateStatus(sku, secondaryStatuses);
    }

    @PatchMapping(value = "/inventories/{sku}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void partialUpdateInventory(@RequestBody Map<String, Object> value, @PathVariable("sku") String sku) throws InventoryNotFoundException {
        this.inventoryService.patchInventory(sku, value);
    }

}
