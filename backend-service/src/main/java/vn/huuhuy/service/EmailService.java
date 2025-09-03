package vn.huuhuy.service;

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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailService {
    @Value("${spring.sendgrid.fromEmail}")
    private String from;
    @Value("${spring.sendgrid.templateID}")
    private String templateID;
    @Value("${spring.sendgrid.verificationLink}")
    private String verificationLink;

    private final SendGrid sendGrid;

    /**
     * Send email by Sendgrid
     * @param to sendEmail to someone
     * @param subject
     * @param text
     */
    public void send(String to, String subject , String text){
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);

        Content content = new Content("text/plain",text);
        Mail mail = new Mail(fromEmail,subject,toEmail,content);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            if(response.getStatusCode() == 202){ //accepted
                log.info("Email sent successfully");
            }else {
                log.error("Email sent failed");
            }
        } catch (IOException e) {
            log.error("Error occured while sending email , error: {}",e.getMessage());
        }
    }

    /**
     * Email verification by SendGrid
     * @param to
     * @param name
     */
    public void emailVerification(String to , String name){
        log.info("Email verification started");

        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);

        String subject = "Xác thực tài khoản";

        String serectCode = String.format("?secretCode=%s", UUID.randomUUID());

        //TODO generate secretCode and save database

        Map<String,String> map = new HashMap<>();
        map.put("name",name);
        map.put("verification_link",verificationLink+serectCode);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        //Add dynamic data
        map.forEach(personalization::addDynamicTemplateData);

        mail.addPersonalization(personalization);
        mail.setTemplateId(templateID);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            if(response.getStatusCode() == 202){ //accepted
                log.info("Verification sent successfully");
            }else {
                log.error("Verification sent failed");
            }
        } catch (IOException e) {
            log.error("Error occured while sending email , error: {}",e.getMessage());
        }
    }



}
