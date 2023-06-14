package com.instamart.orderservice.service;

import com.instamart.orderservice.dto.InventoryResponse;
import com.instamart.orderservice.dto.OrderLineItemRequest;
import com.instamart.orderservice.dto.OrderRequest;
import com.instamart.orderservice.event.OrderPlacedEvent;
import com.instamart.orderservice.model.Order;
import com.instamart.orderservice.model.OrderLineItem;
import com.instamart.orderservice.repository.OrderRepository;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import brave.Span;
import brave.Tracer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final WebClient.Builder webClientBuilder;
  private final Tracer tracer;
  private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

  @CircuitBreaker(name = "inventory", fallbackMethod = "fallBackHandler")
  @TimeLimiter(name = "inventory")
  @Retry(name = "inventory")
  public CompletableFuture<String> placeOrder(OrderRequest orderRequest) {

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

    Span inventoryCheck = tracer.nextSpan().name("InventoryCheck");
    try(Tracer.SpanInScope inventorySpan = tracer.withSpanInScope(inventoryCheck.start())) {

      // Before we place order, we check the inventory first for stock
      InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
              .uri("http://Inventory/api/inventory",
                      uriBuilder -> uriBuilder.queryParam("itemCodes", orderLineItems).build())
              .retrieve()
              .bodyToMono(InventoryResponse[].class)
              .block();

      boolean allItemsInStock = Arrays.stream(inventoryResponses)
              .allMatch(InventoryResponse::isInStock);


      return CompletableFuture.supplyAsync(() -> saveOrder(allItemsInStock, order));
    }
    finally {
      inventoryCheck.finish();
    }

  }

  private String saveOrder(boolean allItemsInStock, Order order)
  {
    if(allItemsInStock) {
      orderRepository.save(order);
      kafkaTemplate.send("orderNotification",new OrderPlacedEvent(order.getOrderNumber()));
      return "Order placed successfully with id "+order.getId();
    }
    else
    {
      return "Product not in stock.";
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

  public CompletableFuture<String> fallBackHandler(OrderRequest orderRequest, RuntimeException runtimeException)
  {
    return CompletableFuture
            .supplyAsync(() -> "Something went wrong when trying to fetch inventory data. " +
                    "Please try again after some time.");
  }
}
