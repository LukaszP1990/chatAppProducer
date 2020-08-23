package com.rabbitmq.chat.producer.service;

import com.rabbitmq.chat.producer.core.MessageUtil;
import com.rabbitmq.chat.producer.dao.MessageRepository;
import com.rabbitmq.chat.producer.dto.MessageDto;
import com.rabbitmq.chat.producer.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = "20000")
@ActiveProfiles("test")
class MessageHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll().thenMany(Flux.fromIterable(MessageUtil.getMessages()))
                .flatMap(message -> messageRepository.save(message))
                .doOnNext(item -> System.out.println("Inserted item: " + item.getReceiver()))
                .blockLast();
    }

    @Test
    void shouldFindAllMessagesBySender() {
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/api/message-producer/sender/messages")
                                .queryParam("sender", MessageUtil.SENDER)
                                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageDto.class)
                .hasSize(4)
                .consumeWith(response ->
                        response.getResponseBody()
                                .forEach(messageDto ->
                                        assertEquals(MessageUtil.MESSAGE, messageDto.getMessage()))
                );
    }

    @Test
    void shouldFindAllMessagesByReceiver() {
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/api/message-producer/receiver/messages")
                                .queryParam("receiver", MessageUtil.RECEIVER)
                                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageDto.class)
                .hasSize(4)
                .consumeWith(response ->
                        response.getResponseBody()
                                .forEach(messageDto ->
                                        assertEquals(MessageUtil.MESSAGE, messageDto.getMessage()))
                );
    }

    @Test
    void shouldSendMessage() {
        var messageDto = getMessageDto();
        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/api/message-producer")
                                .build())
                .body(BodyInserters.fromObject(messageDto))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    private MessageDto getMessageDto() {
        return MessageDto.builder()
                .createdDate(new Date())
                .message("test msg")
                .sender("sender")
                .receiver("receiver")
                .build();
    }
}