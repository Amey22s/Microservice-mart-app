package com.instamart.inventoryservice.repository;

import com.instamart.inventoryservice.dto.InventoryResponse;
import com.instamart.inventoryservice.model.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
  @Query("SELECT i FROM Inventory i WHERE i.itemCode IN :itemCodes")
  List<Inventory> findByItemCodeIn(@Param("itemCodes") List<String> itemCodes);
}
