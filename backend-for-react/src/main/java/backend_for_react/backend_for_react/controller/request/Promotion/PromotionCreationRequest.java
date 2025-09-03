package backend_for_react.backend_for_react.controller.request.Promotion;

import backend_for_react.backend_for_react.common.enums.DiscountType;
import backend_for_react.backend_for_react.model.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class PromotionCreationRequest implements Serializable {
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
