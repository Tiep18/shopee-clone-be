package com.pt.service.implement;

import com.pt.dto.req.PurchaseAddReqDTO;
import com.pt.dto.res.PurchaseResDTO;
import com.pt.entity.Account;
import com.pt.entity.Product;
import com.pt.entity.Purchase;
import com.pt.exception.NotFoundException;
import com.pt.repository.AccountRepository;
import com.pt.repository.ProductRepository;
import com.pt.repository.PurchaseRepository;
import com.pt.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private ProductRepository productRepo;

    @Override
    public List<PurchaseResDTO> getAll(int status, Authentication authentication) {
        Account account = accountRepo.findByEmail(authentication.getName());
        List<Purchase> purchases = status == 0
                ? purchaseRepo.findAllByAccount_Id(account.getId())
                : purchaseRepo.findAllByStatusAndAccount_Id(status, account.getId());
        return purchases.stream().map(PurchaseResDTO::new).collect(Collectors.toList());
    }

    @Override
    public PurchaseResDTO getPurchaseById(int id) {
        Optional<Purchase> purchase = purchaseRepo.findById(id);
        if (purchase.isEmpty()) throw new NotFoundException("Purchase not found");
        return new PurchaseResDTO(purchase.get());
    }

    @Override
    public PurchaseResDTO addToCart(PurchaseAddReqDTO purchaseAddDTO, Authentication authentication) {
        Account account = accountRepo.findByEmail(authentication.getName());
        Optional<Product> product = productRepo.findById(purchaseAddDTO.getProductId());
        if (product.isEmpty()) throw new NotFoundException("Product not found with id " +
                purchaseAddDTO.getProductId());

        LocalDateTime now = LocalDateTime.now();

        // check if purchase not in database -> create new purchase
        Optional<Purchase> purchaseOpt = purchaseRepo.findByAccount_IdAndProduct_IdAndStatus(
                account.getId(), product.get().getId(), -1);
        Purchase purchase = purchaseOpt.orElseGet(Purchase::new);

        // save purchase to database
        purchase.setAccount(account);
        purchase.setProduct(product.get());
        purchase.setStatus(-1);
        purchase.setBuyCount(purchaseOpt.isEmpty()
                ? purchaseAddDTO.getBuyCount()
                : purchase.getBuyCount() + purchaseAddDTO.getBuyCount());
        purchase.setPrice(product.get().getPrice());
        purchase.setPriceBeforeDiscount(product.get().getPriceBeforeDiscount());
        purchase.setUpdatedAt(now);
        if (purchaseOpt.isEmpty()) purchase.setCreatedAt(now);
        purchaseRepo.save(purchase);
        return new PurchaseResDTO(purchase);
    }

    @Override
    public PurchaseResDTO updatePurchase(PurchaseAddReqDTO purchaseAddDTO, Authentication authentication) {
        Account account = accountRepo.findByEmail(authentication.getName());
        Optional<Product> product = productRepo.findById(purchaseAddDTO.getProductId());
        if (product.isEmpty()) throw new NotFoundException("Product not found with id " +
                purchaseAddDTO.getProductId());

        LocalDateTime now = LocalDateTime.now();

        Optional<Purchase> purchaseOpt = purchaseRepo.findByAccount_IdAndProduct_IdAndStatus(
                account.getId(), product.get().getId(), -1);
        if (purchaseOpt.isEmpty()) throw new NotFoundException("Purchase not found");

        Purchase purchase = purchaseOpt.get();

        purchase.setBuyCount(purchaseAddDTO.getBuyCount());
        purchase.setUpdatedAt(now);
        purchaseRepo.save(purchase);
        return new PurchaseResDTO(purchase);
    }

    @Override
    public List<PurchaseResDTO> buyProducts(List<PurchaseAddReqDTO> purchaseDTOs, Authentication authentication) {
        Account account = accountRepo.findByEmail(authentication.getName());
        return purchaseDTOs.stream().map(purchaseDTO -> {
            Optional<Purchase> purchaseOpt = purchaseRepo.findByAccount_IdAndProduct_IdAndStatus(
                    account.getId(), purchaseDTO.getProductId(), -1
            );
            if (purchaseOpt.isEmpty())
                throw new NotFoundException("Product not found in your cart with id " + purchaseDTO.getProductId());
            Purchase purchase = purchaseOpt.get();

            purchase.setStatus(1);
            purchase.setBuyCount(purchaseDTO.getBuyCount());
            purchase.setUpdatedAt(LocalDateTime.now());
            purchaseRepo.save(purchase);
            return new PurchaseResDTO(purchase);
        }).toList();
    }

    @Override
    public void delete(List<Integer> purchaseIds, Authentication authentication) {
        Account account = accountRepo.findByEmail(authentication.getName());
        List<Purchase> purchases = purchaseRepo.findAllById(purchaseIds);
        purchases = purchases.stream().filter(p ->
                p.getStatus() == -1 && p.getAccount().getId() == account.getId()).collect(Collectors.toList());
        purchaseRepo.deleteAll(purchases);
    }

}
