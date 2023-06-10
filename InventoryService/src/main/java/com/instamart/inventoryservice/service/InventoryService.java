package com.instamart.inventoryservice.service;

import com.instamart.inventoryservice.dto.InventoryResponse;
import com.instamart.inventoryservice.model.Inventory;
import com.instamart.inventoryservice.repository.InventoryRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

  private final InventoryRepository inventoryRepository;

  @Transactional(readOnly = true)
  public List<InventoryResponse> isInStock(List<String> itemCode)
  {

    for(String ic : inventoryRepository.findByItemCodeIn(itemCode).stream()
            .map(Inventory::getItemCode).toList()) {
      log.info("Items returned by jpa are " +ic);
    }
    return inventoryRepository.findByItemCodeIn(itemCode)
            .stream().map(inventory ->
                    InventoryResponse.builder()
                            .itemCode(inventory.getItemCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build())
            .toList();
  }
}
