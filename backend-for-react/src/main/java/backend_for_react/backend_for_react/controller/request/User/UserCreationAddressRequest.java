package backend_for_react.backend_for_react.controller.request.User;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreationAddressRequest {
    @NotNull(message = "id must be not blank")
    @Min(value = 1, message = "userID must be equals or greater than 1")
    private Long id;

    @NotNull(message = "id must be not blank")
    @Min(value = 1, message = "province invalid")
    private String provinceId;

    @NotNull(message = "id must be not blank")
    @Min(value = 1, message = "district invalid")
    private String districtId;

    @NotNull(message = "id must be not blank")
    @Min(value = 1, message = "ward invalid")
    private String wardId;

    @NotNull(message = "id must be not blank")
    private String streetAddress;


}
