package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.OTPType;
import backend_for_react.backend_for_react.common.enums.VerificationMethod;
import backend_for_react.backend_for_react.model.OTP;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findByCodeAndUserAndUsedFalseAndTypeAndMethod(String code, User user, OTPType type , VerificationMethod method);
    Optional<OTP> findLatestByUserAndMethodAndType(User user, VerificationMethod method, OTPType type);

    @Transactional
    @Modifying
    @Query("UPDATE OTP o SET o.used = true WHERE o.user = :user AND o.method = :method AND o.type = :type AND o.used = false")
    void invalidateOldCodes(
            @Param("user") User user,
            @Param("method") VerificationMethod method,
            @Param("type") OTPType type
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM OTP o WHERE o.expiryTime < :now")
    void deleteExpiredOTPs(@Param("now") LocalDateTime now);
}