package org.example.service;

import javassist.NotFoundException;
import org.example.entity.Store;
import org.example.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        Optional<List<Store>> storeOptionalList = storeRepository.findStoreByStoreName(store.getStoreName());
        if(storeOptionalList.isPresent()){
            List<Store> storeList = storeOptionalList.get();
            for (Store st : storeList) {
                if (st.getLocation().equalsIgnoreCase(store.getLocation())) {//if name && location are same -> error
                    throw new IllegalStateException("A store with the same name already exists at the same location.");
                }
            }
        }
        storeRepository.save(store); //(name already exists or not) && location is diff -> save
    }

    public Store updateStore(Long storeId, Store updatedStore, boolean updateName, boolean updateLocation) throws NotFoundException {
        Optional<Store> existingStore = getStoreById(storeId);
        if (existingStore.isEmpty()) {
            throw new NotFoundException("Store with ID: " + storeId + " does not exist");
        }
        Store storeToUpdate = existingStore.get();
        if (updateName) {
            storeToUpdate.setStoreName(updatedStore.getStoreName());
        }
        if (updateLocation) {
            storeToUpdate.setLocation(updatedStore.getLocation());
        }
        return storeRepository.save(storeToUpdate);
    }
}
