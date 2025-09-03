package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.common.enums.OTPType;
import backend_for_react.backend_for_react.service.VerifyOTPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL - CONTROLLER")
public class EmailController {
    private final VerifyOTPService verifyOTPService;

    @PostMapping("/send-email")
    public void send(@RequestParam String to,@RequestParam String subject,@RequestParam String content){
        log.info("Sending email to {}",to);
        verifyOTPService.send(to, subject, content);

    }

    @PostMapping("/otp-email")
    public void send(@RequestParam String to,@RequestParam String code){
        log.info("Sending email to {}",to);
        verifyOTPService.sendEmailVerification(to, code, OTPType.VERIFICATION);
    }
}
