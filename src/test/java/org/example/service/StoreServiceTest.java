package org.example.service;

import javassist.NotFoundException;
import org.example.entity.Store;
import org.example.repository.StoreRepository;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;
    @InjectMocks
    StoreService storeService;

    private Store store;
    @BeforeEach
    void setUp() {
        store = Store.builder()
                .storeId(1L)
                .storeName("Safeway")
                .location("Robson Street")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }
    @Test
    @DisplayName("Should return all stores when repository is not empty")
    void testGetAllStores() {
        List<Store> stores = Arrays.asList(store);
        when(storeRepository.findAll()).thenReturn(stores);

        List<Store> result = storeService.getAllStores();

        assertThat(result).hasSize(1).containsExactly(store);
        verify(storeRepository, times(1)).findAll();
    }
    @Test
    @DisplayName("Should return empty list when no stores are available")
    void testGetAllStoresWhenStoresListIsEmpty() {
        when(storeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Store> result = storeService.getAllStores();

        assertThat(result).hasSize(0);
        verify(storeRepository, times(1)).findAll();
    }
    @Test
    @DisplayName("Should return store by Id when store exists")
    void testGetStoreById() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        Optional<Store> result = storeService.getStoreById(1L);

        assertThat(result).isPresent().contains(store);
        verify(storeRepository, times(1)).findById(1L);
    }
    @Test
    @DisplayName("Should return empty optional when store Id is not found")
    void testGetStoreByIdWhenIdNotFound() {
        when(storeRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Store> result = storeService.getStoreById(2L);

        assertThat(result).isEmpty();
        verify(storeRepository, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Should delete store by Id")
    void testDeleteStore() {
        doNothing().when(storeRepository).deleteById(1L);

        storeService.deleteStore(1L);

        verify(storeRepository, times(1)).deleteById(1L);
    }
    @Test
    @DisplayName("Should create store when name & location are unique")
    void testCreateStore() {
        when(storeRepository.findStoreByStoreName(anyString())).thenReturn(Optional.empty());
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        storeService.createStore(store);

        verify(storeRepository, times(1)).findStoreByStoreName(anyString());
        verify(storeRepository, times(1)).save(any(Store.class));
    }
    @ParameterizedTest
    @MethodSource("provideStoreNamesAndLocations")
    @DisplayName("Should create store with same name but different location")
    void testCreateStoreSameNameDifferentLocation(String name, String location) {
        Store newStore = Store.builder()
                        .storeName(name)
                        .location(location)
                        .build();
        when(storeRepository.findStoreByStoreName((anyString()))).thenReturn(Optional.empty());
        when(storeRepository.save(any(Store.class))).thenReturn(newStore);

        storeService.createStore(newStore);

        verify(storeRepository, times(1)).findStoreByStoreName(anyString());
        verify(storeRepository, times(1)).save(any(Store.class));
        assertThat(store.getLocation()).isNotEqualToIgnoringCase(newStore.getLocation());
    }
    @Test
    @DisplayName("Should throw exception when creating store with same name & location")
    void testCreateStoreSameNameSameLocation() {
        Store newStore = Store.builder()
                        .storeName("Safeway")
                        .location("Robson Street")
                        .build();

        when(storeRepository.findStoreByStoreName(anyString())).thenReturn(Optional.of(Arrays.asList(store)));

        assertThatThrownBy(() -> {
            storeService.createStore(newStore);
        }).isInstanceOf(IllegalStateException.class)
                        .hasMessage("A store with the same name already exists at the same location.");

        verify(storeRepository, times(1)).findStoreByStoreName(anyString());
        verify(storeRepository, never()).save(any(Store.class));
        assertThat(store.getLocation()).isEqualToIgnoringCase(newStore.getLocation());
    }
    @Test
    @DisplayName("Should update store name & location")
    void testUpdateStoreNameAndLocation() throws NotFoundException {
        Store updatedStore = Store.builder()
                .storeName("Walmart")
                .location("North Van")
                .build();
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(updatedStore);

        Store resultingStore = storeService.updateStore(1L, updatedStore, true, true);

        verify(storeRepository, times(1)).save(any(Store.class));
        verify(storeRepository, times(1)).findById(anyLong());
        assertThat(resultingStore.getStoreName()).isEqualToIgnoringCase(updatedStore.getStoreName());
        assertThat(resultingStore.getLocation()).isEqualToIgnoringCase(updatedStore.getLocation());
    }
    @Test
    @DisplayName("Should update store name only")
    void testUpdateStoreName() throws NotFoundException {
        Store updatedStoreOnlyName = Store.builder()
                .storeName("Sam's Club")
                .location("Robson Street")
                .build();
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(updatedStoreOnlyName);

        Store resultingStore = storeService.updateStore(1L, updatedStoreOnlyName, true, false);

        verify(storeRepository, times(1)).save(any(Store.class));
        verify(storeRepository, times(1)).findById(anyLong());
        assertThat(resultingStore.getStoreName()).isEqualToIgnoringCase(updatedStoreOnlyName.getStoreName());
        assertThat(resultingStore.getLocation()).isEqualToIgnoringCase(store.getLocation());
    }
    @Test
    @DisplayName("Should update store location only")
    void testUpdateStoreLocation() throws NotFoundException {
        Store updateStoreOnlyLocation = Store.builder()
                .storeName("Safeway")
                .location("Burnaby")
                .build();
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(updateStoreOnlyLocation);

        Store resultingStore = storeService.updateStore(1L, updateStoreOnlyLocation, false, true);

        verify(storeRepository, times(1)).save(any(Store.class));
        verify(storeRepository, times(1)).findById(anyLong());
        assertThat(resultingStore.getStoreName()).isEqualToIgnoringCase(store.getStoreName());
        assertThat(resultingStore.getLocation()).isEqualToIgnoringCase(updateStoreOnlyLocation.getLocation());
    }
    @Test
    @DisplayName("Should throw exception when updating store and Id not found")
    void testUpdateStoreIdNotFound() throws NotFoundException {
        Store updatedStore = Store.builder()
                        .storeName("Store giga")
                        .location("Hello Street")
                        .build();
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> {
            storeService.updateStore(1L, updatedStore, true, true);
        }).isInstanceOf(NotFoundException.class)
                .hasMessage("Store with ID 1 does not exist");

        verify(storeRepository, times(1)).findById(anyLong());
        verify(storeRepository, never()).save(any(Store.class));
    }
    private static Stream<Arguments> provideStoreNamesAndLocations() {
        return Stream.of(
                Arguments.of("Safeway", "Davie Street"),
                Arguments.of("Safeway", "Daviee Street")
        );
    }
}