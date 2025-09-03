package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse implements Serializable {
    Long id;
    String userName;
    String fullName;
    Gender gender;
    LocalDate dateOfBirth;
    String email;
    String phone;
    Set<RoleResponse>roles;
}
