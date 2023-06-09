package com.instamart.inventoryservice.service;

import com.instamart.inventoryservice.dto.InventoryResponse;
import com.instamart.inventoryservice.repository.InventoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

  private final InventoryRepository inventoryRepository;

  @Transactional(readOnly = true)
  public List<InventoryResponse> isInStock(List<String> itemCode)
  {
    return inventoryRepository.findByItemCodeIn(itemCode)
            .stream().map(inventory ->
                    InventoryResponse.builder()
                            .itemCode(inventory.getItemCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build())
            .toList();
  }
}
