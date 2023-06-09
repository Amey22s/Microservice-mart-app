package com.instamart.inventoryservice.repository;

import com.instamart.inventoryservice.dto.InventoryResponse;
import com.instamart.inventoryservice.model.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
  List<Inventory> findByItemCodeIn(List<String> itemCode);
}
