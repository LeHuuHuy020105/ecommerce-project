package backend_for_react.backend_for_react.controller.request.Order;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import lombok.Data;

@Data
public class ChangeOrderStatusRequest {
    private DeliveryStatus status;
}

