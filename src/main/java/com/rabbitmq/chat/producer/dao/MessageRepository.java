package com.rabbitmq.chat.producer.dao;

import com.rabbitmq.chat.model.domain.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

    Flux<Message> findBySender(String sender);

    Flux<Message> findByReceiver(String receiver);
}
