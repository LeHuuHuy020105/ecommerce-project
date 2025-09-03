package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.OTPType;
import backend_for_react.backend_for_react.common.enums.VerificationMethod;
import backend_for_react.backend_for_react.model.OTP;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.OTPRepository;
import backend_for_react.backend_for_react.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "OTP - SERVICE")
public class OTPService {
    private final OTPRepository otpRepository;
    private final VerifyOTPService verifyOTPService;
    private final UserRepository userRepository;

    @Value("${spring.sendgrid.otp-valid-minutes}")
    private int OTP_VALIDITY_MINUTES;

    @Value("${spring.sendgrid.resend-cooldown-minutes}")
    private int RESEND_COOLDOWN_MINUTES;


    /***
     * Create and send to user
     */

    public void generateAndSendOTP(User user , OTPType type , VerificationMethod method) {
        // Kiểm tra cool-down
        if (isInCoolDownPeriod(user, method, type)) {
            throw new RuntimeException("Vui lòng đợi " + RESEND_COOLDOWN_MINUTES + " phút trước khi gửi lại mã");
        }

        // Vô hiệu hóa các mã cũ
        invalidateOldCodes(user, method, type);


        // Tạo mã mới
        String code = generateOTP();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES);
        String destination = getDestination(user, method);

        OTP verificationCode = OTP.builder()
                .code(code)
                .destination(destination)
                .method(method)
                .type(type)
                .expiryTime(expiryTime)
                .user(user)
                .used(false)
                .build();

        otpRepository.save(verificationCode);
        // Gửi mã theo phương thức
        verifyOTPService.sendOTP(destination,code,method,type);
    }
    /***
     * Verify otp
     */

    public boolean verifyOTP(User user, String code , OTPType type , VerificationMethod method) {
        Optional<OTP> otpOptional = otpRepository.findByCodeAndUserAndUsedFalseAndTypeAndMethod(code,user,type,method);
        if(otpOptional.isPresent()){
            OTP otp = otpOptional.get();

            if(otp.isExpired()){
                throw new RuntimeException("The verification code has expired");
            }

            // Tick used otp

            otp.setUsed(true);
            otpRepository.save(otp);

            // Cập nhật trạng thái xác thực của user
            updateUserVerificationStatus(user, method);

            log.info("OTP verified successfully for user: {}", user.getEmail());
            return true;
        }
        log.warn("Invalid OTP for user: {}", user.getEmail());
        return false;
    }

    /**
     * Tạo mã OTP ngẫu nhiên 6 chữ số
     */
    private String generateOTP() {
        Random random = new Random();
        int num = random.nextInt(999999);
        return String.format("%06d", num);
    }

    /**
     * Cập nhật trạng thái xác thực của user
     */
    private void updateUserVerificationStatus(User user, VerificationMethod method) {
        switch (method) {
            case EMAIL:
                user.setEmailVerified(true);
                break;
            case PHONE:
                user.setPhoneVerified(true);
                break;
        }
        userRepository.save(user);
    }

    private String getDestination(User user, VerificationMethod method) {
        switch (method) {
            case EMAIL: return user.getEmail();
            case PHONE: return user.getPhone();
            default: throw new IllegalArgumentException("Phương thức không hợp lệ");
        }
    }

    private void invalidateOldCodes(User user, VerificationMethod method, OTPType type) {
        otpRepository.invalidateOldCodes(user, method, type);
    }

    /**
     * Lịch trình dọn dẹp OTP hết hạn (chạy mỗi giờ)
     */
    @Scheduled(fixedRate = 3600000) // 1 giờ
    public void cleanupExpiredOTPs() {
        try {
            otpRepository.deleteExpiredOTPs(LocalDateTime.now());
            log.info("Cleaned up {} expired OTPs");
        } catch (Exception e) {
            log.error("Error cleaning up expired OTPs", e);
        }
    }

    private boolean isInCoolDownPeriod(User user, VerificationMethod method, OTPType type) {
        Optional<OTP> lastCode = otpRepository
                .findLatestByUserAndMethodAndType(user, method , type);

        return lastCode.isPresent() &&
                lastCode.get().getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(RESEND_COOLDOWN_MINUTES));
    }

}
