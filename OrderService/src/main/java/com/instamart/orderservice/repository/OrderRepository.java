package com.instamart.orderservice.repository;

import com.instamart.orderservice.model.Order;

import org.hibernate.dialect.MySQLStorageEngine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
