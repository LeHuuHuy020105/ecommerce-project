package vn.huuhuy.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import vn.huuhuy.common.enums.Gender;
import vn.huuhuy.common.enums.UserType;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Getter
public class UserCreationRequest implements Serializable {
    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotBlank(message = "firstName must be not blank")
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;
    @Email(message = "Email invalid")
    private String email;
    private String phone;
    private UserType userType;
    private List<AddressRequest> address;
}
