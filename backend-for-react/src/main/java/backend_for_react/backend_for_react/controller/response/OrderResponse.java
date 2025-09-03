package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.PaymentType;
import backend_for_react.backend_for_react.model.OrderItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long id;
    Long userId;
    String customerName;
    String customerPhone;
    String shippingAddress;
    BigDecimal totalAmount;
    DeliveryStatus deliveryStatus;
    PaymentType paymentType;
    List<OrderItemResponse> orderItemResponses;
}
