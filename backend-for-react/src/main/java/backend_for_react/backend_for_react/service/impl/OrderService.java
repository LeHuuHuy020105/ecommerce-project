package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.request.Order.ChangeOrderStatusRequest;
import backend_for_react.backend_for_react.controller.request.Order.OrderCreationRequest;
import backend_for_react.backend_for_react.controller.request.Order.OrderItem.OrderItemCreationRequest;
import backend_for_react.backend_for_react.controller.response.OrderItemResponse;
import backend_for_react.backend_for_react.controller.response.OrderResponse;
import backend_for_react.backend_for_react.controller.response.ProductVariantResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.OrderItemRepository;
import backend_for_react.backend_for_react.repository.OrderRepository;
import backend_for_react.backend_for_react.repository.ProductRepository;
import backend_for_react.backend_for_react.repository.ProductVariantRepository;
import backend_for_react.backend_for_react.service.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    ProductRepository productRepository;
    OrderItemRepository orderItemRepository;
    SecurityUtils securityUtils;
    ProductVariantRepository productVariantRepository;
    ProductService productService;

    @Transactional
    public Long save(OrderCreationRequest req) {
        Order newOrder = new Order();
        User currentUser = securityUtils.getCurrentUser();
        if(currentUser != null){
            newOrder.setUser(currentUser);
        }
        newOrder.setCustomerName(req.getCustomerName());
        newOrder.setCustomerPhone(req.getCustomerPhone());
        newOrder.setShippingAddress(req.getShippingAddress());
        newOrder.setPaymentType(req.getPaymentType());
        newOrder.setOrderStatus(DeliveryStatus.PENDING);
        orderRepository.save(newOrder);
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (OrderItemCreationRequest orderItem : req.getOrderItems()) {
            ProductVariant productVariant = productVariantRepository.findById(orderItem.getProductVariantId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.PRODUCT_VARIANT_NOT_FOUND));
            productVariant.setQuantity(productVariant.getQuantity() - orderItem.getQuantity());
            productVariantRepository.save(productVariant);
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setQuantity(orderItem.getQuantity());
            newOrderItem.setOrder(newOrder);
            newOrderItem.setProductVariant(productVariant);
            orderItemRepository.save(newOrderItem);
            BigDecimal itemTotal = productVariant.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }
        newOrder.setTotalAmount(totalPrice);
        orderRepository.save(newOrder);
        return newOrder.getId();
    }

    public void changeStatus(Long orderId, ChangeOrderStatusRequest req) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.ORDER_NOT_FOUND));
        order.setOrderStatus(req.getStatus());
        orderRepository.save(order);
        if (req.getStatus().equals(DeliveryStatus.CONFIRMED)) {
            for (OrderItem orderItem : order.getOrderItems()) {
                ProductVariant productVariant = orderItem.getProductVariant();
                Product product = productRepository.findById(productVariant.getProduct().getId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.PRODUCT_NOT_FOUND));
                product.setSoldQuantity(product.getSoldQuantity() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }
    }
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.ORDER_NOT_FOUND));
        return getOrderResponse(order);
    }
    public OrderResponse getOrderResponse(Order order) {
        List<OrderItemResponse> orderItemResponseList = order.getOrderItems().stream()
                .map(this::getOrderItemResponse)
                .toList();
        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .paymentType(order.getPaymentType())
                .totalAmount(order.getTotalAmount())
                .deliveryStatus(order.getOrderStatus())
                .orderItemResponses(orderItemResponseList)
                .build();
    }
    public OrderItemResponse getOrderItemResponse(OrderItem orderItem) {
        ProductVariantResponse productVariantResponse = productService.getProductVariantResponse(orderItem.getProductVariant());
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .productVariantResponse(productVariantResponse)
                .quantity(orderItem.getQuantity())
                .build();
    }
}
