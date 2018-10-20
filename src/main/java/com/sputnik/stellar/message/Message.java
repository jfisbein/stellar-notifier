package com.sputnik.stellar.message;

public class Message {
    private final String subject;
    private final String body;

    public Message(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }
}
