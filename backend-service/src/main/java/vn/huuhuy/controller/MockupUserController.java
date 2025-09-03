package vn.huuhuy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.huuhuy.controller.request.UserCreationRequest;
import vn.huuhuy.controller.request.UserPasswordRequest;
import vn.huuhuy.controller.request.UserUpdateRequest;
import vn.huuhuy.controller.response.UserResponse;
import vn.huuhuy.common.enums.Gender;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mockup/user")
@Tag(name = "User Controller")
public class MockupUserController {

    @GetMapping("/list")
    @Operation(summary = "Get user list" , description = "API retrieve user from db")
    public Map<String,Object> findAll(@RequestParam(required = false) String keyword,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam (defaultValue = "20") int size){

        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1L);
        userResponse1.setFirstName("Huu");
        userResponse1.setLastName("Huy");
        userResponse1.setGender(Gender.MALE);
        userResponse1.setEmail("lhhuy2005@gmail.com");
        userResponse1.setPhone("1234567890");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2L);
        userResponse2.setFirstName("Huu");
        userResponse2.setLastName("Huy1");
        userResponse2.setGender(Gender.MALE);
        userResponse2.setEmail("lhhuy20055@gmail.com");
        userResponse2.setPhone("1234567891");

        List<UserResponse> userResponses = List.of(userResponse1,userResponse2);

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","user list");
        result.put("data",userResponses);

        return result;
    }
    @GetMapping("/{userID}")
    @Operation(summary = "Get user detail" , description = "API retrieve user detail by ID ")
    public Map<String,Object> findAll(@PathVariable Long userID){
        UserResponse userDetail = new UserResponse();
        userDetail.setId(userID);
        userDetail.setFirstName("Huu");
        userDetail.setLastName("Huy1");
        userDetail.setGender(Gender.MALE);
        userDetail.setEmail("lhhuy20055@gmail.com");
        userDetail.setPhone("1234567891");

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","user detail");
        result.put("data",userDetail);
        return result;
    }
    @PostMapping("/add")
    @Operation(summary = "Create user " , description = "API add new user to db ")
    public Map<String,Object> createUser(UserCreationRequest request ){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message","user created successfull");
        result.put("data",3);
        return result;
    }

    @PutMapping("/update")
    @Operation(summary = "Update user " , description = "API update new user to db ")
    public Map<String,Object>updateUser(UserUpdateRequest request){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message","user created successfull");
        result.put("data","");
        return result;
    }

    @PatchMapping("/change-pwd")
    @Operation(summary = "Change password " , description = "API change password for user to db ")
    public Map<String,Object>changePassword(UserPasswordRequest request){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message","Password updated successfull");
        result.put("data","");
        return result;
    }

    @GetMapping("/{userID}/delete")
    @Operation(summary = "Delete user " , description = "API delete user to db ")
    public Map<String,Object> deleteUser(@PathVariable Long userID){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message","user deleted successfull");
        result.put("data","");
        return result;
    }
}
