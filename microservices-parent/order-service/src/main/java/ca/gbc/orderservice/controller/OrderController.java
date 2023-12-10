package ca.gbc.orderservice.controller;

import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.service.OrderServiceImpl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.concurrent.CompletedFuture;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderServiceImpl orderService;


//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
//    public String placeOrder(@RequestBody OrderRequest request) {
//        orderService.placeOrder(request);
//        return "Order Placed Successfully";
//    }
//
//    public String placeOrderFallback(OrderRequest request, Exception e) {
//        log.error("Exception is: {}", e.getMessage());
//        return "FALLBACK INVOKED: Order Placed Failed. Please try again later";
//    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest request) {
        orderService.placeOrder(request);
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(request));
    }

    public CompletableFuture<String> placeOrderFallback(OrderRequest request, Exception e) {
        log.error("Exception is: {}", e.getMessage());
        return CompletableFuture.supplyAsync(() -> "FALLBACK INVOKED: Order Placed Failed. Please try again later");
    }
}
