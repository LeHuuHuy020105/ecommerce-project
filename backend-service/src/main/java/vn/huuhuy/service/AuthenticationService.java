package vn.huuhuy.service;

import vn.huuhuy.controller.request.SignInRequest;
import vn.huuhuy.controller.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse getAccessToken(SignInRequest request);
    TokenResponse getRefreshToken(SignInRequest request);
}
