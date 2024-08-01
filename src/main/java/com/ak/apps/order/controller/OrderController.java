package com.ak.apps.order.controller;

import com.ak.apps.order.model.Item;
import com.ak.apps.order.model.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SERVICE_NAME = "catalog-service";
    private static final String ADDRESS_SERVICE_URL = "http://localhost:8082/catalog/item/";

    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "fallbackMethod")
    @PostMapping("/order")
    public ResponseEntity<String> order(@RequestBody Order order){
        System.out.println("Inside order...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        int itemId = order.getId();
        ResponseEntity<List<Item>> exchange = restTemplate.exchange(
                ADDRESS_SERVICE_URL+itemId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Item>>() {
                });

        String str = "Success";
        ResponseEntity<String> response = new ResponseEntity<>(str, HttpStatus.OK);

        return response;

    }

    public ResponseEntity<String> fallbackMethod(Order order, Exception e){
        System.out.println("Exception : "+e.getMessage());
        return new ResponseEntity<String>("ID unknown", HttpStatus.FORBIDDEN);
    }


}
