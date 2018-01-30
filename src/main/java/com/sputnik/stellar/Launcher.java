package com.sputnik.stellar;

import com.sputnik.stellar.mailer.Mailer;
import com.sputnik.stellar.util.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.requests.RequestBuilder.Order;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

public class Launcher {
    private static final ConfigManager config = new ConfigManager(new File(System.getProperty("user.home"), ".stellar-notifier"));
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
    private Mailer mailer = null;

    public static void main(String[] args) throws IOException, InterruptedException {
        new Launcher().launch();
        demonize();
    }

    private static void demonize() {
        // Let the app run forever
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void launch() throws IOException, InterruptedException {
        initMailer();
        KeyPair account = KeyPair.fromAccountId(config.get("AccountId"));

        Server server = new Server("https://horizon.stellar.org");

        PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(account).order(Order.ASC);
        String lastToken = config.get("lastPagingToken");
        if (lastToken != null) {
            paymentsRequest.cursor(lastToken);
        }

        paymentsRequest.stream(operation -> {
                    config.set("lastPagingToken", operation.getPagingToken());
                    if (operation instanceof PaymentOperationResponse) {
                        PaymentOperationResponse paymentOperation = (PaymentOperationResponse) operation;

                        if (paymentOperation.getTo().getAccountId().equals(account.getAccountId())) {
                            String amount = paymentOperation.getAmount();
                            String asset = getAssetName(paymentOperation.getAsset());
                            String from = paymentOperation.getFrom().getAccountId();
                            Date date = getPaymentDate(server, paymentOperation);
                            sendMessage(amount, asset, from, date);
                        }
                    }
                }
        );
    }

    private Date getPaymentDate(Server server, PaymentOperationResponse paymentOperation) {
        Date date = null;

        try {
            String value = server.transactions().transaction(paymentOperation.getLinks().getTransaction().getUri()).getCreatedAt();
            date = Date.from(Instant.parse(value));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return date;
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

    private void sendMessage(String amount, String asset, String from, Date date) {
        logger.info("Sending message");
        Message message = new MimeMessage(mailer.getSession());
        try {
            message.setSubject("New Stellar operation");
            message.setFrom(new InternetAddress(config.get("mail.user")));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(config.get("mail.recipient")));
            message.setText(String.format("Received %s %s from %s at %tc", amount, asset, from, date));
            mailer.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String getAssetName(Asset asset) {
        String assetName;
        if (asset.equals(new AssetTypeNative())) {
            assetName = "lumens";
        } else {
            StringBuilder assetNameBuilder = new StringBuilder();
            assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getCode());
            assetNameBuilder.append(":");
            assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getIssuer().getAccountId());
            assetName = assetNameBuilder.toString();
        }

        return assetName;
    }

    private String loadLastPagingToken() {
        return null;
    }
}
