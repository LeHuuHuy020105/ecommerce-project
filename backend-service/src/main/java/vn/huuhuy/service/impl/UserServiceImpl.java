package vn.huuhuy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.huuhuy.common.enums.UserStatus;
import vn.huuhuy.controller.request.UserCreationRequest;
import vn.huuhuy.controller.request.UserPasswordRequest;
import vn.huuhuy.controller.request.UserUpdateRequest;
import vn.huuhuy.controller.response.UserPageResponse;
import vn.huuhuy.controller.response.UserResponse;
import vn.huuhuy.exception.InvalidDataException;
import vn.huuhuy.exception.ResourceNotFoundException;
import vn.huuhuy.model.entity.Address;
import vn.huuhuy.model.entity.User;
import vn.huuhuy.model.entity.UserHasAddress;
import vn.huuhuy.repository.AddressRepository;
import vn.huuhuy.repository.UserRepository;
import vn.huuhuy.service.EmailService;
import vn.huuhuy.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UserPageResponse findAll(String keyword , String sort, int page, int size) {

        Sort.Order order = new Sort.Order(Sort.Direction.ASC,"id");
        if(sort != null && !sort.isEmpty()){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); //tencot:asc||desc
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    order = new Sort.Order(Sort.Direction.ASC,columnName);
                }else {
                    order = new Sort.Order(Sort.Direction.DESC,columnName);
                }
            }
        }
        // xu li truong hop fe muon truyen tu 1
        int pageNo =0;
        if(page > 0){
            pageNo = page - 1;
        }
        //paging
        Pageable pageable = PageRequest.of(pageNo,size,Sort.by(order));
        Page<User> users = null;
        if(keyword == null || keyword.isEmpty()){
            users = userRepository.findAll(pageable);
        }else {
            keyword = "%"+keyword.toLowerCase()+"%";
            users = userRepository.searchByKeyword(keyword,pageable);
        }
        UserPageResponse response = getUserPageResponse(page, size, users);
        return response;
    }

    @Override
    public UserResponse findByID(Long id) {
        log.info("Find user by id : ",id);
        User user = getUser(id);
        return UserResponse.builder()
                .id(id)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public UserResponse findByEmail(String email) {
        User userByEmail = userRepository.findByEmail(email);
        return UserResponse.builder()
                .id(userByEmail.getId())
                .firstName(userByEmail.getFirstName())
                .lastName(userByEmail.getLastName())
                .gender(userByEmail.getGender())
                .dateOfBirth(userByEmail.getDateOfBirth())
                .email(userByEmail.getEmail())
                .phone(userByEmail.getPhone())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(UserCreationRequest req) {
        log.info("Saving user:{}",req);
        User userByEmail = userRepository.findByEmail(req.getEmail());
        if (userByEmail != null){
            throw new InvalidDataException("Email already exists");
        }

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setType(req.getUserType());
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        if (user.getId() != null){
            List<UserHasAddress> userHasAddresses = new ArrayList<>();
            req.getAddress().forEach(address ->{
                Address addressEntity = new Address();
                UserHasAddress userHasAddress = new UserHasAddress();
                addressEntity.setCity(address.getCity());
                addressEntity.setCountry(address.getCountry());
                addressEntity.setLatitude(address.getLatitude());
                addressEntity.setLongtitude(address.getLongtitude());
                addressEntity.setPostalCode(address.getPostalCode());
                addressEntity.setState(address.getState());
                addressEntity.setStreet(address.getStreet());
                userHasAddress.setUser(user);
                userHasAddress.setAddressType(address.getAddressType());
                userHasAddress.setIsDefault(address.isDefault());
                userHasAddress.setAddress(addressEntity);
                userHasAddresses.add(userHasAddress);
            });
            addressRepository.saveAll(userHasAddresses);
            log.info("Saved addresses : {}", userHasAddresses);
        }
        //send-email
        try {
            emailService.emailVerification(req.getEmail(), req.getFirstName());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("Saving user:{}",req);
        User user = getUser(req.getId());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UserPasswordRequest req) {
        log.info("Changing user:{}",req);

        User user = getUser(req.getId());
        if(req.getPassword().equals(req.getConfirmPassword())){
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        userRepository.save(user);
        log.info("Changed user:{}",req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        User user = getUser(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }
    private User getUser(Long id){
        return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found"));
    }
    private static UserPageResponse getUserPageResponse(int page, int size, Page<User> users) {
        List<UserResponse> userList = users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()

        ).toList();

        UserPageResponse response = new UserPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(users.getTotalElements());
        response.setTotalPages(users.getTotalPages());
        response.setData(userList);
        return response;
    }
}
