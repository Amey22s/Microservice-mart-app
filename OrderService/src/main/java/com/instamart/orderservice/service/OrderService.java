package com.instamart.orderservice.service;

import com.instamart.orderservice.dto.InventoryResponse;
import com.instamart.orderservice.dto.OrderLineItemRequest;
import com.instamart.orderservice.dto.OrderRequest;
import com.instamart.orderservice.model.Order;
import com.instamart.orderservice.model.OrderLineItem;
import com.instamart.orderservice.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final WebClient.Builder webClientBuilder;
  public void placeOrder(OrderRequest orderRequest) {

    List<OrderLineItem> orderLineItemList = orderRequest.getItems()
            .stream().
            map(this::mapToLineItemDTO)
            .toList();

    Order order = new Order();
    order.setOrderNumber(UUID.randomUUID().toString());
    order.setItems(orderLineItemList);

    List<String> orderLineItems  = order.getItems()
            .stream()
            .map(OrderLineItem::getItemCode)
            .toList();

    // Before we place order, we check the inventory first for stock
    InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
            .uri("http://Inventory/api/inventory",
                    uriBuilder -> uriBuilder.queryParam("itemCodes",orderLineItems).build())
            .retrieve()
            .bodyToMono(InventoryResponse[].class)
            .block();

    boolean allItemsInStock = Arrays.stream(inventoryResponses)
            .allMatch(InventoryResponse::isInStock);


    if(allItemsInStock) {
      orderRepository.save(order);
      log.info("Order placed successfully with id {}",order.getId());
    }
    else
    {
      throw new RuntimeException("Product not in stock.");
    }

  }

  private OrderLineItem mapToLineItemDTO(OrderLineItemRequest orderLineItemRequest)
  {
    return OrderLineItem.builder()
            .itemCode(orderLineItemRequest.getItemCode())
            .quantity(orderLineItemRequest.getQuantity())
            .price(orderLineItemRequest.getPrice())
            .build();
  }
}
