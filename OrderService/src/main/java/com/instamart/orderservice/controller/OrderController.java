package com.instamart.orderservice.controller;

import com.instamart.orderservice.dto.InventoryResponse;
import com.instamart.orderservice.dto.OrderRequest;
import com.instamart.orderservice.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest)
  {
    return orderService.placeOrder(orderRequest);
  }
}
