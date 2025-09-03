package backend_for_react.backend_for_react.common.utils;

import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject(); // username
        }

        return null;
    }
    public User getCurrentUser(){
        String username = getCurrentUsername();
        if (username != null) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        return null;
    }
}
