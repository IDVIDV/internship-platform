package ru.internship.platform.service;

import ru.internship.platform.repository.UserRepository;
import ru.internship.platform.dto.Report;
import ru.internship.platform.entity.Task;
import ru.internship.platform.entity.User;
import ru.internship.platform.entity.status.UserInternshipStatus;
import ru.internship.platform.mapper.TaskMapper;
import ru.internship.platform.mapper.UserMapper;
import ru.internship.platform.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;
    public Report getReport(Integer internshipId) {
        List<Task> tasks = taskRepository.findAllByLessonInternshipId(internshipId);
        List<User> users = userRepository.findAllByUserInternshipsInternshipIdAndUserInternshipsStatus(
                internshipId,
                UserInternshipStatus.JOINED
        );

        return new Report(userMapper.toUserDtoList(users), taskMapper.toTaskItemList(tasks));
    }
}
