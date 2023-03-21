package com.olx.inventoryManagementSystem.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.olx.inventoryManagementSystem.controller.dto.InventoryRequest;
import com.olx.inventoryManagementSystem.controller.dto.InventoryResponse;
import com.olx.inventoryManagementSystem.controller.dto.SecondaryStatus;
import com.olx.inventoryManagementSystem.repository.JPAUserRepository;
import com.olx.inventoryManagementSystem.repository.UserRepository;
import com.olx.inventoryManagementSystem.service.InventoryService;
import com.olx.inventoryManagementSystem.service.LoginUserService;
import com.olx.inventoryManagementSystem.utils.JwtUtil;
import com.olx.inventoryManagementSystem.utils.LoadByUsername;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean({InventoryService.class})
@WebMvcTest(controllers = InventoryController.class)
class InventoryControllerTest {

    @MockBean
    InventoryService inventoryService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    LoginUserService loginUserService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    JPAUserRepository jpaUserRepository;

    @MockBean
    LoadByUsername loadByUsername;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        InventoryController inventoryController = new InventoryController(inventoryService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(inventoryController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void ShouldReturnCreateInventoryResponse() throws Exception {
        when(inventoryService.createInventory(new InventoryRequest("bike", "mumbai", dummyAttributes(), 450000, dummyStatuses()))).thenReturn("d59fdbd5-0c56-4a79-8905-6989601890be");
        String expectedResponse = "{\"sku\":\"d59fdbd5-0c56-4a79-8905-6989601890be\"}";

        MockHttpServletRequestBuilder postRequest = post("/inventories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parseFile("src/test/java/com/olx/inventoryManagementSystem/controller/testData/InventoryRequest.json"));

        this.mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedResponse));
        verify(inventoryService, times(1)).createInventory(new InventoryRequest("bike", "mumbai", dummyAttributes(), 450000, dummyStatuses()));
    }

    @Test
    void ShouldFetchListOfInventoriesFromGetInventoriesApi() throws Exception {
        List<InventoryResponse> expectedInventories = List.of(dummyInventoryResponse(dummyAttributes(), dummyStatuses()));
        when(inventoryService.getInventories(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("sku"))))).thenReturn(expectedInventories);
        String expectedResponse = new String(parseFile("src/test/java/com/olx/inventoryManagementSystem/controller/testData/InventoriesResponse.json"));

        MockHttpServletRequestBuilder request = get("/inventories");

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse.replace("\n", "").replace(" ", "")));
        verify(inventoryService, times(1)).getInventories(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("sku"))));
    }

    @Test
    void ShouldFetchInventoryFromGetApiForParticularSku() throws Exception {
        when(inventoryService.getInventory("d59fdbd5-0c56-4a79-8905-6989601890be")).thenReturn(dummyInventoryResponse(dummyAttributes(), dummyStatuses()));
        String expectedResponse = parseFile("src/test/java/com/olx/inventoryManagementSystem/controller/testData/InventoryResponse.json");

        MockHttpServletRequestBuilder requestWithSku = get("/inventories/d59fdbd5-0c56-4a79-8905-6989601890be");

        this.mockMvc.perform(requestWithSku)
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
        verify(inventoryService, times(1)).getInventory("d59fdbd5-0c56-4a79-8905-6989601890be");
    }

    @Test
    void ShouldReturnNoContentHttpStatusCodeForStatusUpdate() throws Exception {
        doNothing().when(inventoryService).updateStatus("d59fdbd5-0c56-4a79-8905-6989601890be", dummyStatuses());

        MockHttpServletRequestBuilder putRequest = put("/inventories/d59fdbd5-0c56-4a79-8905-6989601890be/statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parseFile("src/test/java/com/olx/inventoryManagementSystem/controller/testData/SecondaryStatus.json"));

        this.mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
        verify(inventoryService, times(1)).updateStatus("d59fdbd5-0c56-4a79-8905-6989601890be", dummyStatuses());
    }

    @Test
    void ShouldReturnNoContentHttpStatusCodeForAttributesUpdate() throws Exception {
        doNothing().when(inventoryService).patchInventory("d59fdbd5-0c56-4a79-8905-6989601890be", dummyAttributesMap());

        MockHttpServletRequestBuilder patchRequest = patch("/inventories/d59fdbd5-0c56-4a79-8905-6989601890be")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parseFile("src/test/java/com/olx/inventoryManagementSystem/controller/testData/Attributes.json"));

        this.mockMvc.perform(patchRequest)
                .andExpect(status().isNoContent());
    }

    private String parseFile(String path) throws IOException {
        return (new String(Files.readAllBytes(Paths.get(path))).replace("\n", "").replace(" ",""));
    }

    private static Map<String, Object> dummyAttributesMap(){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", "procured");
        map.put("costPrice", 460000);
        JsonNode attributesValue = new ObjectMapper().createObjectNode();
        ((ObjectNode) attributesValue).put("color", "red");
        ((ObjectNode) attributesValue).put("year", 2021);
        map.put("attributes", attributesValue);
        return map;
    }

    private InventoryResponse dummyInventoryResponse(JsonNode attributes, ArrayList<SecondaryStatus> secondaryStatus) {
        return new InventoryResponse("d59fdbd5-0c56-4a79-8905-6989601890be", "car", "created",
                "Mumbai", LocalDateTime.of(2023, 2, 21, 22, 59),
                LocalDateTime.of(2023, 2, 21, 22, 59), "user",
                "user", attributes, 450000, secondaryStatus);
    }

    private JsonNode dummyAttributes() {
        JsonNode attributes = new ObjectMapper().createObjectNode();
        ((ObjectNode) attributes).put("vin", "AP31CM9873");
        ((ObjectNode) attributes).put("make", "Tata");
        ((ObjectNode) attributes).put("model", "Nexon");
        return attributes;
    }

    private ArrayList<SecondaryStatus> dummyStatuses() {
        ArrayList<SecondaryStatus> secondaryStatus = new ArrayList<>();
        secondaryStatus.add(new SecondaryStatus("warehouse", "in-repair"));
        secondaryStatus.add(new SecondaryStatus("transit", "in-progress"));
        return secondaryStatus;
    }

}
