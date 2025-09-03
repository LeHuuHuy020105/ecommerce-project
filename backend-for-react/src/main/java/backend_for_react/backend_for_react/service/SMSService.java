package backend_for_react.backend_for_react.service;

import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SMSService {
    @Value("${spring.twilio.out-going-sms-number}")
    private String OUTGOING_SMS_NUMBER;

    public void sendSMS(String number, String message) {
        try {
            Message smsMessage = Message.creator(
                    new PhoneNumber(number),
                    new PhoneNumber(OUTGOING_SMS_NUMBER),
                    message
            ).create();

            log.info("SMS sent successfully: SID = {}", smsMessage.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS", e);
        }
    }
}
