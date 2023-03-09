package com.olx.inventoryManagementSystem.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.olx.inventoryManagementSystem.controller.dto.SecondaryStatus;
import com.olx.inventoryManagementSystem.exceptions.InventoryNotFoundException;
import com.olx.inventoryManagementSystem.model.Inventory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryRepositoryTest {

    InventoryRepository inventoryRepository;

    @Mock
    JPAInventoryRepository jpaInventoryRepository;

    @BeforeEach
    void init() {
        inventoryRepository = new InventoryRepository(jpaInventoryRepository);
    }

    @Test
    void ShouldReturnIdFromDB() {
        Inventory inventory = new Inventory("car", "Mumbai", "user", "user", dummyAttributes(), 450000, dummySecondaryStatus());
        when(jpaInventoryRepository.save(inventory)).thenReturn(inventory);

        String actualId = inventoryRepository.createInventory(inventory);

        assertFalse(actualId.isEmpty());
        verify(jpaInventoryRepository, times(1)).save(inventory);
    }

    @Test
    void ShouldReturnInventoryForValidSku() throws InventoryNotFoundException {
        String sku = "d59fdbd5-0c56-4a79-8905-6989601890be";
        Inventory expected = new Inventory("car", "Mumbai", "user", "user", dummyAttributes(), 450000, dummySecondaryStatus());
        when(jpaInventoryRepository.findById(sku)).thenReturn(Optional.of(expected));

        Inventory actual = inventoryRepository.findInventory(sku);

        assertEquals(expected, actual);
        verify(jpaInventoryRepository, times(1)).findById(sku);
    }

    @Test
    void ShouldReturnExceptionForInvalidSku() throws InventoryNotFoundException {
        String sku = "d59fdbd5-0c56-4a79-8905-6989601890be";
        Optional<Inventory> inventory = Optional.empty();
        when(jpaInventoryRepository.findById(sku)).thenReturn(inventory);
        String expectedError = "Inventory not found for sku - " + sku;

        InventoryNotFoundException actualError = Assertions.assertThrows(InventoryNotFoundException.class, () -> inventoryRepository.findInventory(sku));

        assertEquals(expectedError, actualError.getMessage());
        verify(jpaInventoryRepository, times(1)).findById(sku);
    }

    @Test
    void shouldReturnPageOfInventories() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("sku")));
        Page<Inventory> expectedInventories = new PageImpl<>(List.of(new Inventory("car", "Mumbai", "user", "user", dummyAttributes(), 450000, dummySecondaryStatus())));
        when(jpaInventoryRepository.findAll(pageable)).thenReturn(expectedInventories);

        Page<Inventory> actualInventories = inventoryRepository.fetchInventories(pageable);

        assertEquals(expectedInventories, actualInventories);
        verify(jpaInventoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void ShouldReturnSkuAndUpdateStatus() {
        String expectedSku = "d59fdbd5-0c56-4a79-8905-6989601890bf";
        Inventory inventory = new Inventory("car", "Mumbai", "user", "user", dummyAttributes(), 450000, dummySecondaryStatus());
        when(jpaInventoryRepository.save(inventory)).thenReturn(inventory);

        inventoryRepository.saveInventory(inventory);

        verify(jpaInventoryRepository, times(1)).save(inventory);
    }

    private void verifyMocksForUpdateInventory(Inventory inventory) {
        verify(jpaInventoryRepository, times(1)).save(inventory);
    }

    private static ArrayList<SecondaryStatus> dummySecondaryStatus() {
        ArrayList<SecondaryStatus> secondaryStatus = new ArrayList<>();
        secondaryStatus.add(new SecondaryStatus("warehouse", "in-repair"));
        secondaryStatus.add(new SecondaryStatus("transit", "in-progress"));
        return secondaryStatus;
    }

    private static JsonNode dummyAttributes() {
        JsonNode attributes = new ObjectMapper().createObjectNode();
        ((ObjectNode) attributes).put("vin", "AP31CM9873");
        ((ObjectNode) attributes).put("make", "Tata");
        ((ObjectNode) attributes).put("model", "Nexon");
        return attributes;
    }
}
