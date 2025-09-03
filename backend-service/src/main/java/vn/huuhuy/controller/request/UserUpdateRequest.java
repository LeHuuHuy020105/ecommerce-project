package vn.huuhuy.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import vn.huuhuy.common.enums.Gender;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Getter
public class UserUpdateRequest implements Serializable {
    @NotNull(message = "id must be not blank")
    @Min(value = 1, message = "userID must be equals or greater than 1")
    private Long id;
    @NotBlank(message = "firstName must be not blank")
    private String firstName;
    @NotBlank(message = "firstName must be not blank")
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;
    @Email(message = "Email invalid")
    private String email;
    private String phone;
    private List<AddressRequest>address;
}
