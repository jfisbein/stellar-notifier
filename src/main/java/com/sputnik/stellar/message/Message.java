package com.sputnik.stellar.message;

import lombok.Data;

@Data
public class Message {
    private final String subject;
    private final String body;
}
