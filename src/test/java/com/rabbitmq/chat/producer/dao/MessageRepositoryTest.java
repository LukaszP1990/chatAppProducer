package com.rabbitmq.chat.producer.dao;

import com.rabbitmq.chat.model.domain.Message;
import com.rabbitmq.chat.producer.core.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

@DataMongoTest
@ExtendWith(value = MockitoExtension.class)
@ActiveProfiles("test")
class MessageRepositoryTest {

    private static List<Message> messages = MessageUtil.getMessages();

    @Autowired
    private MessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll()
                .thenMany(Flux.fromIterable(messages))
                .flatMap(messageRepository::save)
                .doOnNext(item -> System.out.println("Inserted:" + item.toString()))
                .blockLast();
    }

    @Test
    void getAllMessages() {
        StepVerifier.create(messageRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void getAllMessagesBySender() {
        StepVerifier.create(messageRepository.findBySender(MessageUtil.SENDER))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void getAllMessagesByReceiver() {
        StepVerifier.create(messageRepository.findByReceiver(MessageUtil.RECEIVER))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void saveMessage() {
        StepVerifier.create(messageRepository.save(MessageUtil.createMessage()))
                .expectSubscription()
                .expectNextMatches(message ->
                        Objects.nonNull(message.getId()) &&
                                Objects.nonNull(message.getCreatedDate()) &&
                                MessageUtil.SENDER.equals(message.getSender()) &&
                                MessageUtil.RECEIVER.equals(message.getReceiver()) &&
                                MessageUtil.MESSAGE.equals(message.getMessage())
                ).verifyComplete();
    }
}
