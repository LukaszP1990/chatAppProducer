package com.rabbitmq.chat.producer.mapper;

import com.rabbitmq.chat.model.domain.Message;
import com.rabbitmq.chat.producer.dto.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MessageMapper {

    MessageDto convertToMessageDto(Message message);

    @Mapping(target = "id", ignore = true)
    Message convertToMessage(MessageDto messageDto);
}
