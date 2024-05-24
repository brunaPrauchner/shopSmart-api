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
    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

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
    public ResponseEntity<?> createStore(@RequestBody Store store) {
       storeService.createStore(store);
       return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PutMapping("/updateStore/{storeId}")
    public ResponseEntity<Store> updateStoreWithParams(@PathVariable Long storeId,
                                             @RequestBody Store store,
                                             @RequestParam(required = false, defaultValue = "true") boolean updateName,
                                             @RequestParam(required = false, defaultValue = "true") boolean updateLocation) throws NotFoundException {


            Store updatedStore = storeService.updateStore(storeId, store, updateName, updateLocation);
            return new ResponseEntity<>(updatedStore, HttpStatus.OK);


    }
    @PutMapping("/updateStoreName/{storeId}")
    public ResponseEntity<Store> updateStoreName(@PathVariable Long storeId,
                                                 @RequestBody Store store) throws NotFoundException {
        return updateStoreWithParams(storeId, store, true, false);
    }

    @PutMapping("/updateStoreLocation/{storeId}")
    public ResponseEntity<Store> updateStoreLocation(@PathVariable Long storeId,
                                                     @RequestBody Store store) throws NotFoundException {
        return updateStoreWithParams(storeId, store, false, true);
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