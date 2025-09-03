package backend_for_react.backend_for_react.controller.request.ProductVariant;

import backend_for_react.backend_for_react.controller.request.AttributeValue.AttributeValueCreationRequest;
import backend_for_react.backend_for_react.controller.request.VariantAttribute.VariantAttributeRequest;
import backend_for_react.backend_for_react.model.AttributeValue;
import backend_for_react.backend_for_react.model.VariantAttributeValue;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVariantCreationRequest implements Serializable {
    private BigDecimal price; // Giá
    private Integer quantity; // Số lượng
    private List<VariantAttributeRequest> variantAttributes; // Các thuộc tính
}
