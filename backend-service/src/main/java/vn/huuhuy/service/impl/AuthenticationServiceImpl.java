package vn.huuhuy.service.impl;

import org.springframework.stereotype.Service;
import vn.huuhuy.controller.request.SignInRequest;
import vn.huuhuy.controller.response.TokenResponse;
import vn.huuhuy.service.AuthenticationService;
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        return null;
    }

    @Override
    public TokenResponse getRefreshToken(SignInRequest request) {
        return null;
    }
}
