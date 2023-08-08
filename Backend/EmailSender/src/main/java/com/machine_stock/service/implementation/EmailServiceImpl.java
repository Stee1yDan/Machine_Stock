package com.machine_stock.service.implementation;

import com.machine_stock.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

import static com.machine_stock.utils.EmailUtils.getEmailMessage;
import static com.machine_stock.utils.EmailUtils.getVerificationUrl;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService
{

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String emailSender;
    @Value("${spring.mail.verify.host}")
    private String host;

    @Override
    @Async
    public void sendBasicEmailMessage(String receiverEmail, String token)
    {
        try
        {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("Accounted listed");
            message.setFrom(emailSender);
            message.setTo(receiverEmail);
            message.setText(getEmailMessage(host, token));
            javaMailSender.send(message);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            throw new RuntimeException("Couldn't send basic message");
        }

    }

    @Override
    public void sendMimeMessageWithAttachment(String receiverEmail, String token)
    {
        try
        {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(emailSender);
            helper.setTo(receiverEmail);
            helper.setText(getEmailMessage(host, token));

            //        Add attachments
            String filePath = System.getProperty("user.home") + "\\Downloads\\fort.jpg";
            FileSystemResource fort = new FileSystemResource(new File(filePath));
            System.out.println(filePath);
            helper.addAttachment(fort.getFilename(), fort);
            javaMailSender.send(message);
        }
        catch (Exception exception)
        {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public void sendHtmlPage(String receiverEmail, String token)
    {
        try {
            Context context = new Context();

            context.setVariable("name",receiverEmail.substring(0,receiverEmail.indexOf("@")));
            context.setVariable("url", getVerificationUrl(host, token));

            String text = templateEngine.process("emailtemplate", context);

            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(emailSender);
            helper.setTo(receiverEmail);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }


    private MimeMessage getMimeMessage()
    {
        return javaMailSender.createMimeMessage();
    }

    private String getContentId(String filename)
    {
        return "<" + filename + ">";
    }
}
