package vn.huuhuy.service;

import vn.huuhuy.controller.request.UserCreationRequest;
import vn.huuhuy.controller.request.UserPasswordRequest;
import vn.huuhuy.controller.request.UserUpdateRequest;
import vn.huuhuy.controller.response.UserPageResponse;
import vn.huuhuy.controller.response.UserResponse;

import java.util.List;

public interface UserService {
    UserPageResponse findAll(String keyword , String sort, int page, int size);
    UserResponse findByID(Long id);
    UserResponse findByEmail(String email);
    Long save(UserCreationRequest req);
    void update(UserUpdateRequest req);
    void changePassword(UserPasswordRequest req);
    void delete(Long id);
}
