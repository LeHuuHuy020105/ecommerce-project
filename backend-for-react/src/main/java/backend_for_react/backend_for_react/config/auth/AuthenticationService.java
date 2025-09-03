package backend_for_react.backend_for_react.config.auth;

import backend_for_react.backend_for_react.common.enums.*;
import backend_for_react.backend_for_react.controller.request.Authentication.*;
import backend_for_react.backend_for_react.controller.response.AuthenticationResponse;
import backend_for_react.backend_for_react.controller.response.IntrospectResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.InvalidatedToken;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.InvalidatedTokenRepository;
import backend_for_react.backend_for_react.repository.RoleRepository;
import backend_for_react.backend_for_react.model.Role;
import backend_for_react.backend_for_react.common.enums.RoleType;
import backend_for_react.backend_for_react.repository.UserRepository;
import backend_for_react.backend_for_react.service.OTPService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/***
 * Xu li JWT
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class AuthenticationService {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected Long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Long REFRESHABLE_DURATION;

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    InvalidatedTokenRepository invalidatedTokenRepository;

    OTPService otpService;

    RoleRepository roleRepository;

    public AuthenticationResponse authication (AuthenticationRequest req){
        var user = userRepository.findByUsername(req.getUsername()).orElseThrow(()-> new EntityNotFoundException("User not found"));
        if(user.getStatus().equals(Status.INACTIVE)){
            throw new BusinessException(ErrorCode.UNAUTHENTICATED , "Your account is inactive");
        }
        boolean authenticated =  passwordEncoder.matches(req.getPassword(), user.getPassword());
        if (!authenticated){
            throw new BusinessException(ErrorCode.UNAUTHENTICATED, "Invalid credentials");
        }
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .expiredAt(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .build();

    }

    /***
     * service xac nhan 1 token request co hop le hay khong
     * @param req
     * @return
     * @throws ParseException
     * @throws JOSEException
     */
    @Transactional
    public IntrospectResponse introspect(IntrospectRequest req) throws ParseException, JOSEException {
        String token = req.getToken();
        boolean isValid = true;
        User user = null;
        UserStatus status = null;
        List<String> roles = new ArrayList<>();

        try {
            SignedJWT signedJWT = verifyToken(token, false);
            String username = signedJWT.getJWTClaimsSet().getSubject();

            // Tìm user từ database
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            status = user.getStatus();

            if (!CollectionUtils.isEmpty(user.getRoles())) {
                for (var role : user.getRoles()) {
                    roles.add("ROLE_" + role.getName());
                }
            }
        } catch (BusinessException ex) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .gender(user.getGender())
                .avatar(user.getAvatarImage())
                .status(status)
                .roles(roles)
                .build();
    }

    public void logout(LogoutRequest req) throws ParseException, JOSEException {
        var signedJWT = verifyToken(req.getToken() , true);
        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
    }
    public AuthenticationResponse refreshToken(RefreshRequest req) throws ParseException, JOSEException {
        var signJWT = verifyToken(req.getToken() , true);

        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        var user = userRepository.findByUsername(signJWT.getJWTClaimsSet().getSubject()).orElseThrow(
                ()-> new BusinessException(ErrorCode.NOT_EXISTED , MessageError.USER_NOT_FOUND));
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }
    private SignedJWT verifyToken(String token , boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expityTime = (isRefresh)
                        ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION,ChronoUnit.SECONDS).toEpochMilli())
                        : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        if(!(verified && expityTime.after(new Date()))){
            throw new BusinessException(ErrorCode.UNAUTHENTICATED, MessageError.UNAUTHENTICATED);
        }
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) throw new BusinessException(ErrorCode.UNAUTHENTICATED,MessageError.TOKEN_INVALID);
        return signedJWT;
    }

    private String generateToken(User user){
//        token : header + payload + serectKey => hash => token
//        Thong tin loai token ,thong tin thuat toan , thuat toan HS512
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

//        Noi dung token
//        Data trong body goi la Claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername()) // noi dung dai dien cho user dang nhap he thong
                .issuer("huy.com") // thuong la domain (xac dinh issuer tu ai)
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli())) // thoi gian het hieu luc
                .jwtID(UUID.randomUUID().toString())     // token id
                .claim("scope", buildScope(user)) // thong tin them custom , vi du id , role
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject()); // tao payload object

        JWSObject jwsObject = new JWSObject(header,payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));  // ki bang thuat toan MAC
            return jwsObject.serialize(); // dua ve string
        } catch (Exception e) {
            log.error("can not create token");
            e.printStackTrace();
        }
        return null;
    }
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

    @Transactional
    public void register(RegisterRequest req){
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

        Role role = roleRepository.findByName(RoleType.USER.name())
                .orElseThrow(()-> new BusinessException(ErrorCode.NOT_EXISTED , MessageError.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);

        otpService.generateAndSendOTP(user, OTPType.VERIFICATION, VerificationMethod.EMAIL);
    }
}
