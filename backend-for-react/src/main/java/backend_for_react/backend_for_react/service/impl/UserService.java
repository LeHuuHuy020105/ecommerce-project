package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.OTPType;
import backend_for_react.backend_for_react.common.enums.RoleType;
import backend_for_react.backend_for_react.common.enums.UserStatus;
import backend_for_react.backend_for_react.common.enums.VerificationMethod;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.request.OTP.OTPRequest;
import backend_for_react.backend_for_react.controller.request.User.UserCreationAddressRequest;
import backend_for_react.backend_for_react.controller.request.User.UserCreationRequest;
import backend_for_react.backend_for_react.controller.request.User.UserPasswordRequest;
import backend_for_react.backend_for_react.controller.request.User.UserUpdateRequest;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.RoleResponse;
import backend_for_react.backend_for_react.controller.response.UserResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.Address;
import backend_for_react.backend_for_react.model.Role;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserHasAddress;
import backend_for_react.backend_for_react.repository.AddressRepository;
import backend_for_react.backend_for_react.repository.RoleRepository;
import backend_for_react.backend_for_react.repository.UserHasAddressRepository;
import backend_for_react.backend_for_react.repository.UserRepository;
import backend_for_react.backend_for_react.service.OTPService;
import backend_for_react.backend_for_react.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    AddressRepository addressRepository;
    UserHasAddressRepository userHasAddressRepository;
    RoleRepository roleRepository;
    RoleService roleService;
    OTPService otpService;
    SecurityUtils securityUtils;

    @PreAuthorize("hasRole('ADMIN')")
    // doi voi permission , phan quyen theo permission (nhieu role co nhieu permission nay)
//    @PreAuthorize("hasAuthority('CREATE_DATA')")

    public PageResponse<UserResponse> findAll(String keyword, String sort, int page, int size) {
        log.info("---Find All--");

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username {}",authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); //tencot:asc||desc
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<User> users = null;
        if (keyword == null || keyword.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            keyword = "%" + keyword.toLowerCase() + "%";
            users = userRepository.searchByKeyword(keyword, pageable);
        }
        PageResponse response = getUserPageResponse(pageNo, size, users);
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long save(UserCreationRequest req) {
        log.info("Service create user");
        if(userRepository.existsByUsername(req.getUsername()))
            throw new BusinessException(ErrorCode.EXISTED,MessageError.USERNAME_EXISTED);
        User user = new User();
        user.setFullName(req.getFullName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());
        user.setStatus(UserStatus.NONE);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        Set<Role> roles = new HashSet<>();
        for(Long roleId : req.getRoleId()){
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(()-> new BusinessException(ErrorCode.NOT_EXISTED , MessageError.ROLE_NOT_FOUND));
            roles.add(role);
        }
        user.setRoles(roles);
        userRepository.save(user);

        otpService.generateAndSendOTP(user, OTPType.VERIFICATION, VerificationMethod.EMAIL);
        return user.getId();
    }

    @Transactional
    public void verifyUserAccount(OTPRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(()->new BusinessException(ErrorCode.NOT_EXISTED,MessageError.USER_NOT_FOUND));
        boolean isValid = otpService.verifyOTP(user,request.getCode(),OTPType.VERIFICATION , VerificationMethod.EMAIL);

        if(isValid){
            // ACIVE ACCOUNT
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }else {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Invalid or expired OTP ");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        User user = userRepository.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setFullName(req.getFullName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        
        List<backend_for_react.backend_for_react.model.Role> roles = roleRepository.findAllById(req.getRoles());
        user.setRoles(new HashSet<>(roles));
        userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UserPasswordRequest req) {
        log.info("Changing user:{}", req);

        User user = userRepository.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(req.getPassword());
        }else {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Password and Confirm Password not match");
        }
        userRepository.save(user);
        log.info("Changed user:{}", req);

    }

    @Transactional
    public void forgotPassword(String code , UserPasswordRequest req) {
        User user = securityUtils.getCurrentUser();
        otpService.generateAndSendOTP(user, OTPType.PASSWORD_RESET, VerificationMethod.EMAIL);
        boolean verifyOTP = otpService.verifyOTP(user,code,OTPType.PASSWORD_RESET , VerificationMethod.EMAIL);
        if(verifyOTP){
            if (req.getPassword().equals(req.getConfirmPassword())) {
                user.setPassword(req.getPassword());
            }else {
                throw new BusinessException(ErrorCode.BAD_REQUEST,"Password and Confirm Password not match");
            }
        }else {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Invalid or expired OTP ");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageError.USER_NOT_FOUND));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.delete(user);
    }
//  Kiem tra username tra ve neu trung voi username dang dang nhap thi cho xem hien tai , tranh xem thong tin nguoi khac
//    @PostAuthorize("returnObject.userName == authentication.name")
    public UserResponse getUserById(Long id) {
        log.info("Get user by Id");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED,MessageError.USER_NOT_FOUND));
        UserResponse userResponse = getUserResponse(user);
        return userResponse;
    }

    public UserResponse getMyInfo() {

        //        Lay thong tin user dang dang nhap hien tai qua token
//        get context hien tai
        var context = SecurityContextHolder.getContext();
        var name = context.getAuthentication().getName(); // name user dang dang nhap
        log.info("Username : {}",name);

        User user = userRepository.findByUsername(name).orElseThrow(()-> new EntityNotFoundException("User not found"));
        return getUserResponse(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserAvatar(Long id, String url) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setAvatarImage(url);
        userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addAddress(UserCreationAddressRequest req) {
        User user = userRepository.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Address newAddress = new Address();
        newAddress.setCity(req.getProvinceId());
        newAddress.setDistrict(req.getDistrictId());
        newAddress.setWard(req.getWardId());
        newAddress.setStreet(req.getStreetAddress());

        addressRepository.save(newAddress);

        UserHasAddress userHasAddress = new UserHasAddress();
        userHasAddress.setUser(user);
        userHasAddress.setAddress(newAddress);
        userHasAddress.setIsDefault(true);

        userHasAddressRepository.save(userHasAddress);
    }


    private UserResponse getUserResponse(User user) {
        Set<RoleResponse> roleResponses = new HashSet<>(user.getRoles().stream().map(roleService::getRoleResponse).toList());
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(roleResponses)
                .build();
    }



    private PageResponse<UserResponse> getUserPageResponse(int page, int size, Page<User> users) {
        List<UserResponse> userList = users.stream()
                .map(this::getUserResponse)
                .toList();

        PageResponse<UserResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(users.getTotalElements());
        response.setTotalPages(users.getTotalPages());
        response.setData(userList);
        return response;
    }
}
