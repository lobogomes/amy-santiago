package com.github.lobogomes.amysantiago.service.impl;

import com.github.lobogomes.amysantiago.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;

  @Async
  @Retryable(retryFor = MessagingException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
  @Override
  public CompletableFuture<Integer> sendOtpEmail(String to, String otp) throws MessagingException {
    try {
      sendOtpByEmail(to, otp);
      return CompletableFuture.completedFuture(1);
    } catch (MessagingException e) {
      return CompletableFuture.completedFuture(handleMessagingException(e));
    } catch (UnsupportedEncodingException e) {
      return CompletableFuture.completedFuture(handleUnsupportedEncodingException(e));
    }
  }

  // the method is just to test retry mechanism
  //    public void simulateRandomFailure() throws MessagingException {
  //        int random = (int) (Math.random() * 6 + 1);
  //
  //        log.info("Random number: {}", random);
  //
  //        if (random < 5) { // 4 out of 6 chances to fail
  //            log.error("Simulating a random failure");
  //            throw new MessagingException("Failed to send email");
  //        }
  //    }

  @Recover
  public int handleMessagingException(MessagingException e) {
    log.error("Maximum attempt reached, failed to send email");
    log.error("Error message: {}", e.getMessage());
    return -1;
  }

  @Recover
  public int handleUnsupportedEncodingException(UnsupportedEncodingException e) {
    log.error("Maximum attempt reached , failed to send email");
    log.error("Error message : {}", e.getMessage());
    return -1;
  }

  public void sendOtpByEmail(String to, String otp)
      throws MessagingException, UnsupportedEncodingException {
    log.info("Trying to send email to {}", to);

    String senderName = "ERP";
    String from = "8lobogmes@gmail.com";

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setFrom(from, senderName);
    helper.setTo(to);
    helper.setSubject("One Time Password (OTP) to verify your Email Address");
    String htmlContent =
        "<html>"
            + "<body>"
            + "<p>Dear User,</p>"
            + "<p>The One Time Password (OTP) to verify your Email Address is "
            + "<strong style='font-size:18px; color:blue;'>"
            + otp
            + "</strong>.</p>"
            + "<p>The One Time Password is valid for the next <strong>10 minutes</strong>.</p>"
            + "<p style='color:gray; font-size:12px;'>(This is an auto generated email, so please do not reply back. Email at "
            + "<a href='mailto:8lobogmes@gmail.com'>8lobogmes@gmail.com</a> if you need assistance.)</p>"
            + "<p>Regards,<br/>ERP</p>"
            + "<img src='cid:policeOfficerImage' alt='Police Officer' style='width:100px; height:auto;'/>"
            + "</body>"
            + "</html>";
    helper.setText(htmlContent, true);

    ClassPathResource image = new ClassPathResource("static/security-removebg-preview.png");
    helper.addInline("policeOfficerImage", image);

    mailSender.send(message);
    log.info("Email has been sent successfully to {}", to);
  }
}
