package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.example.entity.Store;
import org.example.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
@ExtendWith(MockitoExtension.class)
public class StoreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Store store;

    @BeforeEach
    void setup(){
        store = Store.builder()
                .storeId(1L)
                .storeName("Safeway")
                .location("Robson Street")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }
    @Test
    @DisplayName("Should return Ok with a list of stores when repository is not empty")
    void testGetAllStores() throws Exception {
        List<Store> stores = Arrays.asList(store);
        when(storeService.getAllStores()).thenReturn(stores);

        mockMvc.perform(get("/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(stores.size()))
                .andExpect(jsonPath("$[0].storeId").value(store.getStoreId())) //[0] indexing bc returns an array
                .andExpect(jsonPath("$[0].storeName").value(store.getStoreName()))
                .andExpect(jsonPath("$[0].location").value(store.getLocation()));

        verify(storeService, times(1)).getAllStores();
    }
    @Test
    @DisplayName("Should return Ok with an empty list when repository is empty")
    void testGetAllStoresWhenStoresListIsEmpty() throws Exception {
        when(storeService.getAllStores()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(storeService, times(1)).getAllStores();
    }
    @Test
    @DisplayName("Should return Ok with a store when Id exists")
    void testGetStoreById() throws Exception {
        when(storeService.getStoreById(1L)).thenReturn(Optional.of(store));

        mockMvc.perform(get("/{storeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(store.getStoreId())) //directly accessing the properties
                .andExpect(jsonPath("$.storeName").value(store.getStoreName()))
                .andExpect(jsonPath("$.location").value(store.getLocation()));

        verify(storeService, times(1)).getStoreById(1L);

    }
    @Test
    @DisplayName("Should return Not Found when Id does not exists")
    void testGetStoreByIdWhenIdNotExists() throws Exception {
        when(storeService.getStoreById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/{storeId}", 2L))
                .andExpect(status().isNotFound());

        verify(storeService, times(1)).getStoreById(2L);
    }
    @Test
    @DisplayName("Should return Created when a store is created")
    void testCreateStore() throws Exception {
        Store newStore = Store.builder()
                .storeId(2L)
                .storeName("Whole Foods")
                .location("Granville Street")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        doNothing().when(storeService).createStore(any(Store.class));

        mockMvc.perform(post("/createStore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStore)))
                .andExpect(status().isCreated());

        verify(storeService, times(1)).createStore(any(Store.class));
    }
    @Test
    @DisplayName("Should return Conflict when trying to create a store that already exists")
    void testCreateStoreWhenDuplicatedName() throws Exception {
        Store sameNameStore = Store.builder()
                .storeId(3L)
                .storeName("Whole Foods")
                .location("Granville Street")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        doNothing().when(storeService).createStore(any(Store.class));

        doThrow(IllegalStateException.class).when(storeService).createStore(any(Store.class));

        mockMvc.perform(post("/createStore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sameNameStore)))
                .andExpect(status().isConflict());

        verify(storeService, times(1)).createStore(any(Store.class));
    }
    @Test
    @DisplayName("Should return Ok when name & location in store are updated")
    void testUpdateStoreNameAndLocation() throws Exception {
        Store updatedStore = Store.builder()
                .storeName("Walmart")
                .location("North Van")
                .build();
        when(storeService.updateStore(eq(1L), any(Store.class), eq(true), eq(true))).thenReturn(updatedStore);

        mockMvc.perform(put("/updateStore/{storeId}", 1L)
                .param("updateName", "true")
                .param("updateLocation", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStore)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value(updatedStore.getStoreName()))
                .andExpect(jsonPath("$.location").value(updatedStore.getLocation()));

        verify(storeService, times(1)).updateStore(eq(1L), any(Store.class), eq(true), eq(true));
    }

    @Test
    @DisplayName("Should return Ok when name in store is updated")
    void testUpdateStoreName() throws Exception {
        Store updatedStore = Store.builder()
                .storeId(1L)
                .storeName("New Walmart")
                .location("Robson Street")
                .createdAt(store.getCreatedAt())
                .build();

        when(storeService.updateStore(eq(1L), any(Store.class), eq(true), eq(false)))
                .thenReturn(updatedStore);

        mockMvc.perform(put("/updateStore/{storeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStore))
                        .param("updateName", "true")
                        .param("updateLocation", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(updatedStore.getStoreId()))
                .andExpect(jsonPath("$.storeName").value(updatedStore.getStoreName()))
                .andExpect(jsonPath("$.location").value(updatedStore.getLocation()));

        verify(storeService, times(1)).updateStore(eq(1L), any(Store.class), eq(true), eq(false));
    }

    @Test
    @DisplayName("Should return Ok when a location in store is updated")
    void testUpdateStoreLocation() throws Exception {
        Store updatedStore = Store.builder()
                .storeId(1L)
                .storeName(store.getStoreName())
                .location("Haro Street")
                .createdAt(store.getCreatedAt())
                .build();

        when(storeService.updateStore(eq(1L), any(Store.class), eq(false), eq(true)))
                .thenReturn(updatedStore);

        mockMvc.perform(put("/updateStore/{storeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStore))
                        .param("updateName", "false")
                        .param("updateLocation", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(updatedStore.getStoreId()))
                .andExpect(jsonPath("$.storeName").value(updatedStore.getStoreName()))
                .andExpect(jsonPath("$.location").value(updatedStore.getLocation()));

        verify(storeService, times(1)).updateStore(eq(1L), any(Store.class), eq(false), eq(true));
    }

    @Test
    @DisplayName("Should return Not Found when trying to update a store that Id does not exists")
    void testUpdateStoreIdNotFound() throws Exception {
        Long nonExistentStoreId = 999L;
        Store storeToUpdate = Store.builder()
                .storeName("Nonexistent Store")
                .location("Unknown Location")
                .build();

        when(storeService.updateStore(nonExistentStoreId, storeToUpdate, true, true))
                .thenThrow(new NotFoundException("Store with ID " + nonExistentStoreId + " does not exist"));

        mockMvc.perform(put("/updateStore/{storeId}", nonExistentStoreId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeToUpdate))
                        .param("updateName", "true")
                        .param("updateLocation", "true"))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Store with ID " + nonExistentStoreId + " does not exist"))
                .andExpect(jsonPath("$.path").value("/" + nonExistentStoreId));
    }

    @Test
    @DisplayName("Should return Ok when a store is deleted")
    void testDeleteStore() throws Exception {
        when(storeService.getStoreById(anyLong())).thenReturn(Optional.of(store));
        doNothing().when(storeService).deleteStore(anyLong());

        mockMvc.perform(delete("/{storeId}", 1L))
                .andExpect(status().isOk());

        verify(storeService, times(1)).getStoreById(anyLong());
        verify(storeService, times(1)).deleteStore(anyLong());
    }

    @Test
    @DisplayName("Should return Not Found when trying to delete a store that Id does not exists")
    void testDeleteStoreWhenIdNotFound() throws Exception {
        when(storeService.getStoreById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/{storeId}", 2L))
                .andExpect(status().isNotFound());

        verify(storeService,times(1)).getStoreById(anyLong());
        verify(storeService, never()).deleteStore(anyLong());
    }
}
