package ca.gbc.orderservice.service;

import ca.gbc.orderservice.dto.OrderRequest;

public interface OrderService {
    String placeOrder(OrderRequest orderRequest);

}
