package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.OTP.OTPRequest;
import backend_for_react.backend_for_react.controller.request.User.UserCreationAddressRequest;
import backend_for_react.backend_for_react.controller.request.User.UserCreationRequest;
import backend_for_react.backend_for_react.controller.request.User.UserPasswordRequest;
import backend_for_react.backend_for_react.controller.request.User.UserUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.UserResponse;
import backend_for_react.backend_for_react.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userService.findAll(keyword, sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getDetailUser(@PathVariable Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user detail");
        result.put("data", userService.getUserById(userId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
        UserResponse result = userService.getMyInfo();
        return ApiResponse.<UserResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<Object> createUser(@RequestBody @Valid UserCreationRequest req) {
        log.info("Controller create user");
        Long userId = userService.save(req);
        return new ApiResponse<Object>(HttpStatus.OK.value(), "create user succesfull", userId);
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<String> uploadAvatar(@PathVariable Long userId,
                                               @RequestParam("avater") String url) throws IOException {
        userService.updateUserAvatar(userId, url);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping("/verify-account")
    public void verifyAccount(@RequestBody OTPRequest req) {
        userService.verifyUserAccount(req);
    }

    @PostMapping("/add/address")
    public ResponseEntity<String> createAddress(@RequestBody @Valid UserCreationAddressRequest req) {
        userService.addAddress(req);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody UserPasswordRequest request , @RequestPart String code){
        userService.forgotPassword(code,request);
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateRequest req) {
        userService.update(req);
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
