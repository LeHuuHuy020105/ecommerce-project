package backend_for_react.backend_for_react.controller.request.Product;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.controller.request.Attribute.AttributeCreationRequest;
import backend_for_react.backend_for_react.controller.request.ImageProduct.ImageProductCreationRequest;
import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantCreationRequest;
import backend_for_react.backend_for_react.model.Attribute;
import backend_for_react.backend_for_react.model.ImageProduct;
import backend_for_react.backend_for_react.model.ImageProductDescription;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreationRequest implements Serializable {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be greater than 0")
    private BigDecimal listPrice;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Cover image is required")
    private String coverImage; // URL string từ dịch vụ upload

    @Size(min = 1, message = "At least 1 product image is required")
    private List<String> imageProduct; // Danh sách URL

    private String video; // URL video

    @Valid
    private List<ProductVariantCreationRequest> productVariant;

    @Valid
    private List<AttributeCreationRequest> attributes;
}