package backend_for_react.backend_for_react.controller.request.VariantAttributeValue;

import lombok.Data;

import java.io.Serializable;

@Data
public class VariantAttributeValueCreationRequest implements Serializable {
    private Long productVariantId;
    private Long attributeValueId;
}
