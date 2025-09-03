package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.OTPType;
import backend_for_react.backend_for_react.common.enums.VerificationMethod;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL - SERVICE")
public class VerifyOTPService {

    @Value("${spring.sendgrid.from-email}")
    private String from;
    @Value("${spring.sendgrid.otp-valid-minutes}")
    private String expiredTimeOTP;

    private final SMSService smsService;

    private final SendGrid sendGrid;

    /***
     * Send email by Sendgrid
     * @param to send email to someone
     * @param subject
     * @param text
     */
    public void send(String to, String subject, String text){
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);

        Content content = new Content("text/plain", text);
        Mail mail = new Mail(fromEmail,subject,toEmail,content);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            if (response.getStatusCode() == 202){
                log.info("Email sent successfully");
            }else {
                log.error("Email sent failed. Status code: {}", response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("Email sent failed: {}", e.getMessage());
        }
    }

    public void sendOTP (String to, String code , VerificationMethod method , OTPType type){
        switch(method){
            case EMAIL :
                sendEmailVerification(to ,code,type);
                break;
            case PHONE:
                sendSMSVerification(to , code, type);
                break;
            default:
                throw new IllegalArgumentException("Verification method not supported");
        }
    }
    /***
     * Send by Email
     * @param to
     */
    public void sendEmailVerification(String to,String code, OTPType otpType){
        log.info("Email verification started for: {}", to);

        try {

            // Tạo email với template
            Mail mail = createVerificationEmail(to, code, otpType);

            // Gửi email
            sendEmailWithTemplate(mail);

            log.info("Verification email sent successfully to: {}", to);

            // Lưu mã xác thực vào database (tuỳ chọn)
            // verificationService.saveVerificationCode(to, verificationCode);

        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("Could not send verification email", e);
        }
    }

    private Mail createVerificationEmail(String to, String verificationCode, OTPType otpType) {
        Email fromEmail = new Email(from, "HUYLE");
        Email toEmail = new Email(to);

        // Tạo đối tượng Mail với template ID
        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setTemplateId("d-1cc4e97fc6e9404eaa0d392665313df2");

        // Tạo personalization để thêm dynamic data
        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        // Xác định tiêu đề và mô tả dựa trên loại OTP
        String subject = "";
        String description = "";

        switch(otpType) {
            case PASSWORD_RESET:
                subject = "Đặt Lại Mật Khẩu";
                description = "Để hoàn tất yêu cầu đặt lại mật khẩu";
                break;
            case VERIFICATION:
                subject = "Xác Thực Email";
                description = "Để xác thực địa chỉ email của bạn";
                break;
            case TWO_FACTOR_AUTH:
                subject = "Xác Thực 2 Lớp";
                description = "Để hoàn tất quá trình đăng nhập";
                break;
            default:
                subject = "Xác Thực OTP";
                description = "Để hoàn tất yêu cầu của bạn";
        }

        // Thêm các biến dynamic cho template
        personalization.addDynamicTemplateData("verification-code", verificationCode);
        personalization.addDynamicTemplateData("subject", subject);
        personalization.addDynamicTemplateData("description", description);
        personalization.addDynamicTemplateData("timer", LocalDateTime.now().plusMinutes(Long.parseLong(expiredTimeOTP)));

        mail.addPersonalization(personalization);
        mail.setSubject(subject);

        return mail;
    }

    /***
     * Send by Phone
     * @param phone
     * @param type
     * @return
     */

    public void sendSMSVerification(String phone, String code, OTPType type) {
        String message = "";

        switch (type) {
            case VERIFICATION:
                message = String.format("HUYLE: Xin chao , ma xac thuc dang ky cua ban la: %s. Hieu luc %s phut.", code ,expiredTimeOTP);
                break;
            case PASSWORD_RESET:
                message = String.format("HUYLE: Xin chao , ma dat lai mat khau cua ban la: %s. Hieu luc %s phut.", code , expiredTimeOTP);
                break;
            case TWO_FACTOR_AUTH:
                message = String.format("HUYLE: Xin chao , ma xac thuc 2 lop cua ban la: %s. Hieu luc %s phut.", code ,expiredTimeOTP);
                break;
        }

        smsService.sendSMS(phone, message);
    }



    private void sendEmailWithTemplate(Mail mail) throws IOException {
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Verification email sent successfully. Status code: {}", response.getStatusCode());
            } else {
                log.error("Failed to send verification email. Status code: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
                throw new IOException("Failed to send email: " + response.getStatusCode());
            }
        } catch (IOException ex) {
            log.error("Error sending verification email: {}", ex.getMessage());
            throw ex;
        }
    }
}