package com.rabbitmq.chat.producer.service;

import com.rabbitmq.chat.producer.dao.MessageRepository;
import com.rabbitmq.chat.producer.dto.MessageDto;
import com.rabbitmq.chat.producer.mapper.MessageMapper;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
class MessageService {

    private static final String QUEUE_NAME = "message_queue";
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final RabbitTemplate rabbitTemplate;

    MessageService(MessageRepository messageRepository,
                   MessageMapper messageMapper,
                   RabbitTemplate rabbitTemplate) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    Mono<List<MessageDto>> findAllMessagesBySender(String sender) {
        log.info("Finding all sender messages: {}", sender);
        return Try.of(() -> sender)
                .map(booleanResult -> messageRepository.findBySender(sender)
                        .map(messageMapper::convertToMessageDto)
                        .collectList())
                .getOrElse(() -> null);
    }

    Mono<List<MessageDto>> findAllMessagesByReceiver(String receiver) {
        log.info("Finding all receiver messages: {}", receiver);
        return Try.of(() -> receiver)
                .map(booleanResult -> messageRepository.findByReceiver(receiver)
                        .map(messageMapper::convertToMessageDto)
                        .collectList())
                .getOrElse(() -> null);
    }

    Mono<MessageDto> sendMessage(MessageDto message) {
        log.info("Save messages: {}", message);
        return Mono.just(message)
                .filter(Objects::nonNull)
                .map(messageMapper::convertToMessage)
                .flatMap(messageRepository::save)
                .doOnSuccess(msg -> {
                    log.info("Message has been sent to queue");
                    rabbitTemplate.convertAndSend(QUEUE_NAME, msg);
                })
                .map(messageMapper::convertToMessageDto);
    }
}
