package com.rabbitmq.chat.producer.core;

import com.rabbitmq.chat.producer.dto.MessageDto;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public class ServerResponseUtil {

    public static Mono<ServerResponse> ok(List<MessageDto> messages) {
        return ServerResponse
                .ok()
                .body(BodyInserters.fromObject(messages));
    }

    public static Mono<ServerResponse> badRequest(MessageError messageError) {
        return ServerResponse
                .badRequest()
                .body(BodyInserters.fromObject(getMessageDto(messageError)));
    }

    private static MessageDto getMessageDto(MessageError messageError) {
        return MessageDto.builder()
                .message(messageError.getText())
                .build();
    }
}
