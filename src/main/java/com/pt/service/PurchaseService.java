package com.pt.service;

import com.pt.dto.req.PurchaseAddReqDTO;
import com.pt.dto.res.PurchaseResDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PurchaseService {
    public List<PurchaseResDTO> getAll(int status, Authentication authentication);

    PurchaseResDTO getPurchaseById(int id);

    PurchaseResDTO addToCart(PurchaseAddReqDTO purchaseAddDTO, Authentication authentication);

    PurchaseResDTO updatePurchase(PurchaseAddReqDTO purchaseAddDTO, Authentication authentication);

    List<PurchaseResDTO> buyProducts(List<PurchaseAddReqDTO> purchaseDTOs, Authentication authentication);

    void delete(List<Integer> purchaseIds, Authentication authentication);
}





