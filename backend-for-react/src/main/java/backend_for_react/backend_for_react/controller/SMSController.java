package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.SMS.SMSSendRequest;
import backend_for_react.backend_for_react.service.SMSService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
@Slf4j(topic = "SMS-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SMSController {
    SMSService smsService;

    @PostMapping("/processSMS")
    public void processSMS(@RequestBody SMSSendRequest request){
        smsService.sendSMS(request.getDestinationSMSNumber(), request.getMessage());
    }
}
