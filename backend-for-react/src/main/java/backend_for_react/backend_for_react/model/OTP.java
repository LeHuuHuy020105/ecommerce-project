package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.OTPType;
import backend_for_react.backend_for_react.common.enums.VerificationMethod;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "otp")
public class OTP extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private Boolean used = false;

    @Enumerated(EnumType.STRING)
    private OTPType type;

    @Enumerated(EnumType.STRING)
    private VerificationMethod method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
