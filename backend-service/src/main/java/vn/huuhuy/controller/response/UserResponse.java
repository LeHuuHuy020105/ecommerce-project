package vn.huuhuy.controller.response;

import lombok.*;
import vn.huuhuy.common.enums.Gender;

import java.io.Serializable;
import java.sql.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;
    private String email;
    private String phone;
//    private List<Role> roles;
}
