package vn.huuhuy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.huuhuy.controller.request.SignInRequest;
import vn.huuhuy.controller.response.TokenResponse;

@RestController
@RequestMapping("/auth")
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {
    @Operation(summary = "Access token", description = "Get access token and refesh token by username and password")
    @PostMapping("/access-token")
    public TokenResponse getAccessToken(@RequestBody SignInRequest request){
        log.info("Access token request");
        return TokenResponse.builder()
                .accessToken("DUMY=ACCESS-TOKEN")
                .refeshToken("DUMY=REFESH-TOKEN")
                .build();
    }

    @Operation(summary = "Refesh token", description = "Get refesh token by username and password")
    @PostMapping("/refesh-token")
    public TokenResponse getRefeshToken(@RequestBody String refeshToken){
        log.info("Refesh token request");
        return TokenResponse.builder()
                .accessToken("DUMY=ACCESS-TOKEN")
                .refeshToken("DUMY=REFESH-TOKEN")
                .build();
    }
}
