package org.example.service;

import javassist.NotFoundException;
import org.example.entity.Store;
import org.example.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:Indentation"})
@Service
public class StoreService {
    private final StoreRepository storeRepository;

    @Autowired
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Optional<Store> getStoreById(Long storeId) {
        return storeRepository.findById(storeId);
    }

    public void deleteStore(Long storeId) {
        storeRepository.deleteById(storeId);
    }

    public void createStore(Store store) {
        storeRepository.save(store);
    }

    public Store updateStore(Long storeId, Store updatedStore) throws NotFoundException {
        Optional<Store> existingStore = getStoreById(storeId);
        if (existingStore.isEmpty()) {
            throw new NotFoundException("Store not found with ID: " + storeId);
        }
        Store storeToUpdate = existingStore.get();
        storeToUpdate.setStoreName(updatedStore.getStoreName());
        storeToUpdate.setLocation(updatedStore.getLocation());
        return storeRepository.save(storeToUpdate);
    }
}
