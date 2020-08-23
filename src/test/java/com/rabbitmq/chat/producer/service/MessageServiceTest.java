package com.rabbitmq.chat.producer.service;

import com.rabbitmq.chat.producer.core.MessageUtil;
import com.rabbitmq.chat.producer.dao.MessageRepository;
import com.rabbitmq.chat.producer.dto.MessageDto;
import com.rabbitmq.chat.producer.mapper.MessageMapper;
import com.rabbitmq.chat.producer.mapper.MessageMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(value = MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ActiveProfiles("test")
class MessageServiceTest {

    private MessageRepository messageRepository = mock(MessageRepository.class);
    private RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    private MessageMapper messageMapper = new MessageMapperImpl();
    private MessageService messageService = new MessageService(messageRepository, messageMapper, rabbitTemplate);

    @Test
    void shouldFindAllMessagesBySender() {
        when(messageRepository.findBySender(anyString()))
                .thenReturn(Flux.just(MessageUtil.createMessage()));

        StepVerifier.create(messageService.findAllMessagesBySender(MessageUtil.SENDER))
                .expectSubscription()
                .expectNextMatches(message -> message.stream()
                        .allMatch(MessageUtil::isMessageMatch))
                .verifyComplete();
    }

    @Test
    void shouldReturnNullAfterTryingToFindAllMessagesBySender() {
        Mono<List<MessageDto>> allMessagesBySender = messageService.findAllMessagesBySender(MessageUtil.SENDER);
        assertNull(allMessagesBySender);
    }

    @Test
    void shouldReturnEmptyMonoAfterTryingToFindAllMessagesBySender() {
        when(messageRepository.findBySender(anyString()))
                .thenReturn(Flux.empty());

        StepVerifier.create(messageService.findAllMessagesBySender(MessageUtil.SENDER))
                .expectSubscription()
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void shouldFindAllMessagesByReceiver() {
        when(messageRepository.findBySender(anyString()))
                .thenReturn(Flux.just(MessageUtil.createMessage()));

        StepVerifier.create(messageService.findAllMessagesBySender(MessageUtil.RECEIVER))
                .expectSubscription()
                .expectNextMatches(message -> message.stream()
                        .allMatch(MessageUtil::isMessageMatch))
                .verifyComplete();
    }

    @Test
    void shouldReturnNullAfterTryingToFindAllMessagesByReceiver() {
        Mono<List<MessageDto>> allMessagesByReceiver = messageService.findAllMessagesByReceiver(MessageUtil.RECEIVER);
        assertNull(allMessagesByReceiver);
    }

    @Test
    void shouldReturnEmptyMonoAfterTryingToFindAllMessagesByReceiver() {
        when(messageRepository.findByReceiver(anyString()))
                .thenReturn(Flux.empty());

        StepVerifier.create(messageService.findAllMessagesByReceiver(MessageUtil.RECEIVER))
                .expectSubscription()
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void sendMessage() {
        when(messageRepository.save(any()))
                .thenReturn(Mono.just(MessageUtil.createMessage()));

        StepVerifier.create(messageService.sendMessage(MessageUtil.createMessageDto()))
                .expectSubscription()
                .expectNextMatches(MessageUtil::isMessageMatch)
                .verifyComplete();
    }
}