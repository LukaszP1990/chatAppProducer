package com.rabbitmq.chat.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor(onConstructor = @__(@Builder))
public class MessageDto {

    private String sender;
    private String receiver;
    private String message;
    private Date createdDate;
}
