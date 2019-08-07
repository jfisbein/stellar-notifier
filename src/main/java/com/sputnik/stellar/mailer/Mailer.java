package com.sputnik.stellar.mailer;

import lombok.Getter;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

public class Mailer {
    @Getter
    protected final Session session;

    public Mailer(Properties mailConfiguration, String username, String password) {

        session = Session.getInstance(mailConfiguration,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public void send(Message msg) throws MessagingException {
        Transport.send(msg);
    }
}
