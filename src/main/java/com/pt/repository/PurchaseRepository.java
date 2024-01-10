package com.pt.repository;

import com.pt.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    List<Purchase> findAllByStatusAndAccount_Id(int status, int accountId);

    List<Purchase> findAllByAccount_Id(int accountId);

    Optional<Purchase> findByAccount_IdAndProduct_Id(int accountId, int productId);

    Optional<Purchase> findByAccount_IdAndProduct_IdAndStatus(int accountId, int productId, int status);
}
