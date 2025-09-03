package backend_for_react.backend_for_react.controller.request.User;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.validator.DobConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest implements Serializable {
    @NotNull(message = "id must be not blank")
    @Min(value = 1, message = "userID must be equals or greater than 1")
    Long id;
    @NotBlank(message = "fullName must be not blank")
    String fullName;
    @NotNull(message = MessageError.GENDER_NOT_BLANK)
    Gender gender;
    @DobConstraint(min = 14)
    LocalDate dateOfBirth;
    @Email(message = "Email invalid")
    @NotBlank(message = "firstName must be not blank")
    String email;
    @NotBlank(message = MessageError.PHONE_NOT_BLANK)
    String phone;
    List<Long> roles;

}
