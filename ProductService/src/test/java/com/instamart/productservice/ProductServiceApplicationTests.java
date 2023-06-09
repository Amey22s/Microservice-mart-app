package com.instamart.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.instamart.productservice.dto.ProductRequest;
import com.instamart.productservice.repository.ProductRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
  static {
    mongoDBContainer.start();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry)
  {
    dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
  }


  @Test
  void createProductTest() throws Exception {
    ProductRequest productRequest = getProductRequest();
    String data = objectMapper.writeValueAsString(productRequest);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
            .contentType(MediaType.APPLICATION_JSON)
            .content(data))
            .andExpect(status().isCreated());

    Assertions.assertEquals(1,productRepository.findAll().size());
  }

  @Test
  void getAllProductsTest() throws Exception {
    ProductRequest productRequest = getProductRequest();
    String data = objectMapper.writeValueAsString(productRequest);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
            .contentType(MediaType.APPLICATION_JSON)
            .content(data));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].name").value("Test Iphone"))
            .andExpect(jsonPath("$.[0].description").value("This is a test product."))
            .andExpect(jsonPath("$.[0].price").value(999.99));
  }

  private ProductRequest getProductRequest()
  {
    return ProductRequest.builder()
            .name("Test Iphone")
            .description("This is a test product.")
            .price(BigDecimal.valueOf(999.99))
            .build();
  }

}
