package com.rabbitmq.chat.producer.service;

import com.rabbitmq.chat.producer.core.MessageError;
import com.rabbitmq.chat.producer.core.ServerResponseUtil;
import com.rabbitmq.chat.producer.dto.MessageDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public
class MessageHandler {

    private static final String SENDER = "sender";
    private static final String RECEIVER = "receiver";
    private final MessageService messageService;

    MessageHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    public Mono<ServerResponse> findAllMessagesBySender(ServerRequest serverRequest) {
        return Mono.just(serverRequest.queryParam(SENDER))
                .flatMap(Mono::justOrEmpty)
                .map(messageService::findAllMessagesBySender)
                .flatMap(senderMessages -> getMessages(senderMessages, MessageError.NO_SENDER_MSG))
                .switchIfEmpty(ServerResponseUtil.badRequest(MessageError.NO_SENDER));
    }

    public Mono<ServerResponse> findAllMessagesByReceiver(ServerRequest serverRequest) {
        return Mono.just(serverRequest.queryParam(RECEIVER))
                .flatMap(Mono::justOrEmpty)
                .map(messageService::findAllMessagesByReceiver)
                .flatMap(senderMessages -> getMessages(senderMessages, MessageError.NO_RECEIVER_MSG))
                .switchIfEmpty(ServerResponseUtil.badRequest(MessageError.NO_RECEIVER));
    }

    public Mono<ServerResponse> sendMessage(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(MessageDto.class)
                .flatMap(messageService::sendMessage)
                .flatMap(message -> ServerResponseUtil.ok(List.of(message)))
                .switchIfEmpty(ServerResponseUtil.badRequest(MessageError.NO_MESSAGE_TO_SAVE));
    }

    private Mono<ServerResponse> getMessages(Mono<List<MessageDto>> senderMessages,
                                             MessageError messageError) {
        return senderMessages
                .filter(messageDtoList -> !messageDtoList.isEmpty())
                .flatMap(ServerResponseUtil::ok)
                .switchIfEmpty(ServerResponseUtil.badRequest(messageError));
    }

}
