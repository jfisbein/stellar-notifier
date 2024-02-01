package com.sputnik.stellar;

import com.sputnik.stellar.mailer.Mailer;
import com.sputnik.stellar.message.Message;
import com.sputnik.stellar.message.MessagesCreator;
import com.sputnik.stellar.util.ConfigManager;
import java.io.File;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.requests.RequestBuilder.Order;
import org.stellar.sdk.responses.operations.OperationResponse;

@Slf4j
public class Launcher {

  private static final ConfigManager config = new ConfigManager(new File(System.getProperty("user.home"), ".stellar-notifier"));
  private Mailer mailer = null;

  public static void main(String[] args) {
    new Launcher().launch();
  }

  private void launch() {
    log.info("Launching Stellar Notifier with configuration:");
    log.info("AccountId: {}", config.get("AccountId"));
    log.info("lastPagingToken: {}", config.get("lastPagingToken"));
    log.info("mail.smtp.auth: {}", config.get("mail.smtp.auth"));
    log.info("mail.smtp.starttls.enable: {}", config.get("mail.smtp.starttls.enable"));
    log.info("mail.smtp.host: {}", config.get("mail.smtp.host"));
    log.info("mail.smtp.port: {}", config.get("mail.smtp.port"));
    log.info("mail.user: {}", config.get("mail.user"));
    log.info("mail.password: **********");

    initMailer();
    String monitoredAccountId = config.get("AccountId");
    try (Server server = new Server("https://horizon.stellar.org")) {
      MessagesCreator messagesCreator = new MessagesCreator();

      PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(monitoredAccountId).order(Order.ASC);
      Optional.ofNullable(config.get("lastPagingToken")).ifPresent(paymentsRequest::cursor);

      paymentsRequest.stream(new EventListener<>() {
        @Override
        public void onEvent(OperationResponse operation) {
          try {
            log.info("Operation Received - Type: {}, Id: {}, SourceAccount: {}, Date: {}", operation.getType(),
              operation.getId(), operation.getSourceAccount(), Date.from(Instant.parse(operation.getCreatedAt())));
            sendMessage(messagesCreator.createMessage(operation, monitoredAccountId));
            config.set("lastPagingToken", operation.getPagingToken());
          } catch (Exception e) {
            log.error("Error trying to send email", e);
          }
        }

        @Override
        public void onFailure(Optional<Throwable> error, Optional<Integer> responseCode) {
          log.warn("{},{}", error.orElse(null), responseCode.orElse(null));
        }
      });

    }
  }

  private void initMailer() {
    Properties props = new Properties();
    props.put("mail.smtp.auth", config.get("mail.smtp.auth"));
    props.put("mail.smtp.starttls.enable", config.get("mail.smtp.starttls.enable"));
    props.put("mail.smtp.host", config.get("mail.smtp.host"));
    props.put("mail.smtp.port", config.get("mail.smtp.port"));
    String username = config.get("mail.user");
    String password = config.get("mail.password");

    mailer = new Mailer(props, username, password);
  }

  private void sendMessage(Message msg) {
    log.info("Sending message");
    javax.mail.Message message = new MimeMessage(mailer.getSession());
    try {
      message.setSubject(msg.getSubject());
      message.setFrom(new InternetAddress(config.get("mail.user")));
      message.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(config.get("mail.recipient")));
      message.setText(msg.getBody());
      mailer.send(message);
    } catch (MessagingException e) {
      log.error(e.getMessage(), e);
    }
  }

  private void waitAndThen(TimeUnit timeUnit, long amount, Runnable runnable) {
    try {
      timeUnit.sleep(amount);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
    }

    runnable.run();
  }
}
