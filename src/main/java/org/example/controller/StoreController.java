package org.example.controller;

import java.util.List;
import java.util.Optional;

import javassist.NotFoundException;
import org.example.entity.*;
import org.example.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:Indentation"})
@RestController
public class StoreController {

    @Autowired
    private StoreService storeService;

   @GetMapping("/stores")
    public ResponseEntity<List<Store>> getAllStores() {
       List<Store> stores = storeService.getAllStores();
       return new ResponseEntity<>(stores, HttpStatus.OK);
   }

    @GetMapping("/{storeId}")
    public ResponseEntity<Optional<Store>> getStoreById(@PathVariable Long storeId) {
        Optional<Store> store = storeService.getStoreById(storeId);
        if (store.isPresent()) {
            return new ResponseEntity<>(store, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/createStore")
    public ResponseEntity<Store> createStore(@RequestBody Store store) {
       storeService.createStore(store);
       return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<Store> updateStore(@PathVariable Long storeId, @RequestBody Store store) throws NotFoundException {
        Optional<Store> existingStore = storeService.getStoreById(storeId);
        if (existingStore.isPresent()) {
            Store updatedStore = storeService.updateStore(storeId, store);
            return new ResponseEntity<>(updatedStore, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long storeId) {
        Optional<Store> store = storeService.getStoreById(storeId);
        if (store.isPresent()) {
            storeService.deleteStore(storeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}