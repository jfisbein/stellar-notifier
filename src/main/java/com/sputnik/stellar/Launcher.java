package com.sputnik.stellar;

import com.sputnik.stellar.mailer.Mailer;
import com.sputnik.stellar.message.Message;
import com.sputnik.stellar.message.MessagesCreator;
import com.sputnik.stellar.util.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.requests.RequestBuilder.Order;
import org.stellar.sdk.requests.SSEStream;
import org.stellar.sdk.responses.operations.OperationResponse;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Launcher {
    private static final ConfigManager config = new ConfigManager(new File(System.getProperty("user.home"), ".stellar-notifier"));
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
    private Mailer mailer = null;

    public static void main(String[] args) {
        new Launcher().launch();
    }

    private void launch() {
        logger.info("Launching Stellar Notifier with configuration:");
        logger.info("AccountId: {}", config.get("AccountId"));
        logger.info("lastPagingToken: {}", config.get("lastPagingToken"));
        logger.info("mail.smtp.auth: {}", config.get("mail.smtp.auth"));
        logger.info("mail.smtp.starttls.enable: {}", config.get("mail.smtp.starttls.enable"));
        logger.info("mail.smtp.host: {}", config.get("mail.smtp.host"));
        logger.info("mail.smtp.port: {}", config.get("mail.smtp.port"));
        logger.info("mail.user: {}", config.get("mail.user"));
        logger.info("mail.password: **********");

        initMailer();
        KeyPair account = KeyPair.fromAccountId(config.get("AccountId"));
        Server server = new Server("https://horizon.stellar.org");
        MessagesCreator messagesCreator = new MessagesCreator();

        while (true) {
            PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(account).order(Order.ASC);
            Optional.ofNullable(config.get("lastPagingToken")).ifPresent(lastToken -> paymentsRequest.cursor(lastToken));

            SSEStream<OperationResponse> stream = paymentsRequest.stream(operation -> {
                        config.set("lastPagingToken", operation.getPagingToken());
                        logger.info("Operation Received - Type: {}, Id: {}, SourceAccount: {}, Date: {}", operation.getType(), operation.getId(), operation.getSourceAccount().getAccountId(), Date.from(Instant.parse(operation.getCreatedAt())));
                        sendMessage(messagesCreator.createMessage(operation, account));
                    }
            );

            waitAndThen(TimeUnit.MINUTES, 30, stream::close);
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
        logger.info("Sending message");
        javax.mail.Message message = new MimeMessage(mailer.getSession());
        try {
            message.setSubject(msg.getSubject());
            message.setFrom(new InternetAddress(config.get("mail.user")));
            message.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(config.get("mail.recipient")));
            message.setText(msg.getBody());
            mailer.send(message);
        } catch (MessagingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void waitAndThen(TimeUnit timeUnit, long amount, Runnable runnable) {
        try {
            timeUnit.sleep(amount);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }

        runnable.run();
    }
}
