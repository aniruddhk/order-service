package com.ak.apps.order.controller;

import com.ak.apps.order.model.Item;
import com.ak.apps.order.model.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${catalog.server.url}")
    private String catalogServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    private static final String SERVICE_NAME = "catalog-service";


    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "fallbackMethod")
    @PostMapping("/order")
    @RateLimiter(name = SERVICE_NAME, fallbackMethod = "fallbackMethodForRateLimi")
    public ResponseEntity<String> order(@RequestBody Order order){
        System.out.println("Inside order...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        logger.info("The catalog service URL is : {}", catalogServiceUrl);

        int itemId = order.getId();
        ResponseEntity<List<Item>> exchange = restTemplate.exchange(
                catalogServiceUrl+itemId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Item>>() {
                });

        String str = "Success";
        ResponseEntity<String> response = new ResponseEntity<>(str, HttpStatus.OK);

        return response;

    }

    @PostMapping("/order-trial")
    @Retry(name = SERVICE_NAME, fallbackMethod = "fallbackMethod")
    public ResponseEntity<String> order1(@RequestBody Order order){
        System.out.println("Inside order...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        logger.info("The catalog service URL is : {}", catalogServiceUrl);

        int itemId = order.getId();
        ResponseEntity<List<Item>> exchange = restTemplate.exchange(
                catalogServiceUrl+itemId,
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

    public ResponseEntity<String> fallbackMethodForRateLimi(Order order, Exception e){
        System.out.println("Exception : "+e.getMessage());
        return new ResponseEntity<String>("TOO_MANY_REQUESTS", HttpStatus.TOO_MANY_REQUESTS);
    }



}
