package com.instamart.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@EnableEurekaClient
public class NotificationServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(NotificationServiceApplication.class, args);
  }

  @KafkaListener(topics = "orderNotification")
  public void handleNotification(OrderPlacedEvent orderPlacedEvent)
  {
    log.info("Order placed event received successfully with order number {}",orderPlacedEvent.getOrderNumber());
  }

}
