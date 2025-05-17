package ru.internship.platform.mapper;

import ru.internship.platform.dto.MessageDto;
import ru.internship.platform.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "senderUsername", source = "sender.username")
    @Mapping(target = "receiverUsername", source = "receiver.username")
    MessageDto toMessageDto(Message message);

    List<MessageDto> toMessageDtoList(List<Message> messageList);
}
