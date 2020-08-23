package com.rabbitmq.chat.producer.core;

import lombok.Getter;

@Getter
public enum MessageError {

    NO_MESSAGE_TO_SAVE("No message to save"),
    NO_RECEIVER("No receiver to get messages"),
    NO_SENDER("No sender to get messages"),
    NO_SENDER_MSG("No sender message has been found in db"),
    NO_RECEIVER_MSG("No receiver message has been found in db");

    private final String text;

    MessageError(String text) {
        this.text = text;
    }
}
