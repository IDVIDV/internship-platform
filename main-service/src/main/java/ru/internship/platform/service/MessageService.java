package ru.internship.platform.service;

import ru.internship.platform.repository.MessageRepository;
import ru.internship.platform.repository.UserRepository;
import ru.internship.platform.dto.MessageDto;
import ru.internship.platform.entity.Comment;
import ru.internship.platform.entity.Message;
import ru.internship.platform.entity.Role;
import ru.internship.platform.entity.Task;
import ru.internship.platform.entity.User;
import ru.internship.platform.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public void noticeStudentOnCommitCheck(@NotNull Comment comment) {
        Message message = new Message();
        message.setReceiver(comment.getUser());
        message.setSendDate(Timestamp.valueOf(LocalDateTime.now()));
        message.setMessageContent("New comment on your commit: " + comment.getCommit().getUrl());
        messageRepository.save(message);
    }

    public void noticeAllAdminsOnForkFail(List<User> users, List<Task> tasks, String errorMessage) {
        List<User> admins = userRepository.findAllByRoleId(Role.ADMIN.getId());
        List<Message> messages = new ArrayList<>();
        StringBuilder userIds = new StringBuilder("(");
        StringBuilder taskIds = new StringBuilder("(");
        users.forEach(user -> userIds.append(user.getId()).append(","));
        tasks.forEach(task -> taskIds.append(task.getId()).append(","));

        if (userIds.length() > 1) {
            userIds.deleteCharAt(userIds.length() - 1);
            userIds.append(")");
        }

        if (taskIds.length() > 1) {
            taskIds.deleteCharAt(userIds.length() - 1);
            taskIds.append(")");
        }

        String messageContent = "Error happened during forking following tasks with ids: " + taskIds +
                " for following user with id: " + userIds +
                " with error message:" + errorMessage;

        for (User admin : admins) {
            Message message = new Message();
            message.setReceiver(admin);
            message.setSendDate(Timestamp.valueOf(LocalDateTime.now()));
            message.setMessageContent(messageContent);
            messages.add(message);
        }

        messageRepository.saveAll(messages);
    }

    public List<MessageDto> getMessagesByReceiverId(Integer receiverId) {
        return messageMapper.toMessageDtoList(messageRepository.findAllByReceiverId(receiverId));
    }
}
