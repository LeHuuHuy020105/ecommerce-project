package backend_for_react.backend_for_react.controller.request.Promotion;

import backend_for_react.backend_for_react.common.enums.DiscountType;
import backend_for_react.backend_for_react.common.enums.Status;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class PromotionUpdateRequest implements Serializable {
    private Long id;
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
