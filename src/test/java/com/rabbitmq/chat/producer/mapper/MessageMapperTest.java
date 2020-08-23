package com.rabbitmq.chat.producer.mapper;

import com.rabbitmq.chat.model.domain.Message;
import com.rabbitmq.chat.producer.core.MessageUtil;
import com.rabbitmq.chat.producer.dto.MessageDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class MessageMapperTest {

    private static final Message message = MessageUtil.createMessage();
    private static final MessageDto messageDto = MessageUtil.createMessageDto();
    private MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    @Test
    void shouldConvertToMessageDto() {
        MessageDto messageDto = messageMapper.convertToMessageDto(message);

        assertNotNull(messageDto);
        assertAll(()->{
            assertNotNull(messageDto.getCreatedDate());
            assertEquals(MessageUtil.SENDER, messageDto.getSender());
            assertEquals(MessageUtil.RECEIVER, messageDto.getReceiver());
            assertEquals(MessageUtil.MESSAGE, messageDto.getMessage());
        });
    }

    @Test
    void shouldConvertToMessage() {

        Message message = messageMapper.convertToMessage(messageDto);

        assertNotNull(message);
        assertAll(()->{
            assertNotNull(message.getCreatedDate());
            assertEquals(MessageUtil.SENDER, message.getSender());
            assertEquals(MessageUtil.RECEIVER, message.getReceiver());
            assertEquals(MessageUtil.MESSAGE, message.getMessage());
        });
    }
}