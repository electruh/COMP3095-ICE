package ca.gbc.orderservice.service;

import ca.gbc.orderservice.dto.InventoryRequest;
import ca.gbc.orderservice.dto.InventoryResponse;
import ca.gbc.orderservice.dto.OrderLineItemDto;
import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.events.OrderPlacedEvent;
import ca.gbc.orderservice.model.Order;
import ca.gbc.orderservice.model.OrderLineItem;
import ca.gbc.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional

public class OrderServiceImpl implements OrderService {

//    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;

    @Value("${inventory.service.url}")
    private String inventoryServiceUri;

    @Override
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems =
                orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToOrderLineItemDto)
                .toList();

        order.setOrderLineItemList(orderLineItems);

       List<InventoryRequest> inventoryRequests =  order.getOrderLineItemList()
                        .stream().map(orderLineItem -> InventoryRequest
                        .builder()
                        .skuCode(orderLineItem.getSkuCode())
                        .quantity(orderLineItem.getQuantity())
                        .build())
                        .toList();

       List<InventoryResponse> inventoryResponseList = webClient
                .build()
                .post()
                .uri(inventoryServiceUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequests)
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();

       assert inventoryResponseList != null;
       boolean allProductsIsInStock = inventoryResponseList
               .stream()
                       .allMatch(InventoryResponse::isSufficientStock);

       if(Boolean.TRUE.equals(allProductsIsInStock)){
           orderRepository.save(order);

//           kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));

           return "Order Placed Successfully";

       }else {
           throw new RuntimeException("Not all products are in stock, order cannot be placed");
       }
    }


    private OrderLineItem mapToOrderLineItemDto (OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        return orderLineItem;
    }

}
