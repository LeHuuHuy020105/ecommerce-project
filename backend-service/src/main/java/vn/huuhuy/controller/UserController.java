package vn.huuhuy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.huuhuy.controller.request.UserCreationRequest;
import vn.huuhuy.controller.request.UserPasswordRequest;
import vn.huuhuy.controller.request.UserUpdateRequest;
import vn.huuhuy.controller.response.UserResponse;
import vn.huuhuy.common.enums.Gender;
import vn.huuhuy.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@RequestMapping("/user")
@Tag(name = "User Controller")
public class UserController {
    private final UserService userService;

    @GetMapping("/list")
    @Operation(summary = "Get user list" , description = "API retrieve user from db")
    public Map<String,Object> findAll(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String sort,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam (defaultValue = "10") int size){

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","user list");
        result.put("data",userService.findAll(keyword, sort, page, size));

        return result;
    }
    @GetMapping("/{userID}")
    @Operation(summary = "Get user detail" , description = "API retrieve user detail by ID ")
    public ResponseEntity<Object> findUserDetail(@PathVariable @Min(value = 1, message = "userID must be equals or greater than 1") Long userID){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message","user created successfull");
        result.put("data",userService.findByID(userID));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @PostMapping("/add")
    @Operation(summary = "Create user " , description = "API add new user to db ")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreationRequest request ){
        Long userId = userService.save(request);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message","user created successfull");
        result.put("data",userId);
        return new ResponseEntity<>(result,HttpStatus.CREATED);
    }

    @PutMapping("/update")
    @Operation(summary = "Update user " , description = "API update new user to db ")
    public ResponseEntity<Object>updateUser(@RequestBody @Valid UserUpdateRequest request){
        userService.update(request);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message","user created successfull");
        result.put("data","");
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }

    @PatchMapping("/change-pwd")
    @Operation(summary = "Change password " , description = "API change password for user to db ")
    public ResponseEntity<Object>changePassword(@RequestBody @Valid UserPasswordRequest request){
        userService.changePassword(request);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message","Password updated successfull");
        result.put("data","");
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }

    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String serectCode , HttpServletResponse response) throws IOException {
        log.info("Confirm email: {}",serectCode);
        try {
            //TODO check or compare serectCode from DB
        } catch (Exception e) {
            log.error("Confirm email was failure! , errorMessage = {}",e.getMessage());
        }finally {
            response.sendRedirect("https://www.google.com/");
        }
    }

    @DeleteMapping("/{userID}/delete")
    @Operation(summary = "Delete user " , description = "API delete user to db ")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userID){
        userService.delete( userID);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message","user deleted successfull");
        result.put("data","");
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
