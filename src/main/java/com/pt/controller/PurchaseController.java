package com.pt.controller;

import com.pt.dto.req.PurchaseAddReqDTO;
import com.pt.dto.res.PurchaseResDTO;
import com.pt.dto.res.ResponseDTO;
import com.pt.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@CrossOrigin
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAll(@RequestParam int status, Authentication authentication) {
        List<PurchaseResDTO> purchases = purchaseService.getAll(status, authentication);
        return ResponseEntity.ok(new ResponseDTO("Get purchases successfully", purchases));
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<ResponseDTO> addToCart(@RequestBody PurchaseAddReqDTO purchaseAddDTO,
                                                 Authentication authentication) {
        PurchaseResDTO purchase = purchaseService.addToCart(purchaseAddDTO, authentication);
        return ResponseEntity.ok(new ResponseDTO("Add purchase to cart successfully", purchase));
    }

    @PutMapping("/update-purchase")
    public ResponseEntity<ResponseDTO> updatePurchase(@RequestBody PurchaseAddReqDTO purchaseAddDTO,
                                                      Authentication authentication) {
        PurchaseResDTO purchase = purchaseService.updatePurchase(purchaseAddDTO, authentication);
        return ResponseEntity.ok(new ResponseDTO("Update purchase successfully", purchase));
    }

    @PostMapping("/buy-products")
    public ResponseEntity<ResponseDTO> buyProducts(@RequestBody List<PurchaseAddReqDTO> purchaseDTOs,
                                                   Authentication authentication) {
        List<PurchaseResDTO> purchases = purchaseService.buyProducts(purchaseDTOs, authentication);
        return ResponseEntity.ok(new ResponseDTO("Buy successfully", purchases));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO> deletePurchases(@RequestBody List<Integer> purchaseIds,
                                                       Authentication authentication) {
        purchaseService.delete(purchaseIds, authentication);
        return ResponseEntity.ok(new ResponseDTO("Delete successfully", null));
    }
}
