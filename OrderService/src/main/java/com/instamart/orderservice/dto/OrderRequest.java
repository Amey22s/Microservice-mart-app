package com.instamart.orderservice.dto;

import com.instamart.orderservice.model.OrderLineItem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
  private List<OrderLineItemRequest> items;
}
