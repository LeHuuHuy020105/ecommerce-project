package backend_for_react.backend_for_react.controller.request.Order;

import backend_for_react.backend_for_react.common.enums.PaymentType;
import backend_for_react.backend_for_react.controller.request.Order.OrderItem.OrderItemCreationRequest;
import backend_for_react.backend_for_react.model.OrderItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderCreationRequest implements Serializable {
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private List<OrderItemCreationRequest>orderItems;
    private PaymentType paymentType;
}
