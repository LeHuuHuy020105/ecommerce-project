package vn.huuhuy.service;

import org.springframework.security.core.GrantedAuthority;
import vn.huuhuy.common.enums.TokenType;

import java.util.Collection;

public interface JwtService {
    String generateAccessToken(Long userId, String username, Collection<? extends GrantedAuthority> authorities);
    String generateRefeshToken(Long userId, String username, Collection<? extends GrantedAuthority> authorities);
    String extractUsername(String token, TokenType type);
}
