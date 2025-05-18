package ru.internship.platform.service;

import ru.internship.platform.exception.ForkFailedException;
import ru.internship.platform.dto.TaskForkDto;
import ru.internship.platform.dto.TaskForkItem;
import ru.internship.platform.entity.Internship;
import ru.internship.platform.entity.Lesson;
import ru.internship.platform.entity.Task;
import ru.internship.platform.entity.TaskFork;
import ru.internship.platform.entity.User;
import ru.internship.platform.entity.status.UserInternshipStatus;
import ru.internship.platform.grpc.GitlabServiceGrpcClient;
import ru.internship.platform.mapper.TaskForkMapper;
import ru.internship.platform.repository.TaskForkRepository;
import ru.internship.platform.repository.UserInternshipRepository;
import ru.internship.platform.repository.projection.UserOnly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskForkService {
    private final GitlabServiceGrpcClient gitlabServiceGrpcClient;
    private final TaskForkRepository taskForkRepository;
    private final UserInternshipRepository userInternshipRepository;
    private final TaskForkMapper taskForkMapper;
    private final MessageService messageService;

    public List<TaskForkItem> getAllTaskForksByTaskId(Integer taskId) {
        return taskForkMapper.toTaskForkItemList(taskForkRepository.findAllByTaskId(taskId));
    }

    public TaskForkDto getTaskForkById(Integer taskForkId) {
        TaskFork taskFork = taskForkRepository.findById(taskForkId).orElse(null);

        if (taskFork == null) {
            throw new EntityNotFoundException(String.format("TaskFork with id %d does not exist", taskForkId));
        }

        return taskForkMapper.toTaskForkDto(taskFork);
    }

    public void acceptTaskFork(Integer taskForkId) {
        TaskFork taskFork = taskForkRepository.findById(taskForkId).orElse(null);

        if (taskFork == null) {
            throw new EntityNotFoundException(String.format("TaskFork with id %d does not exist", taskForkId));
        }

        taskFork.setAccepted(true);

        taskForkRepository.save(taskFork);
    }

    public void createForksForTaskInOngoingInternship(Task task) {
        List<User> users = getUsersInInternship(task.getLesson()
                .getInternship().getId());

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        createForks(users, tasks);
    }

    public void createForksOnInternshipStart(Internship internship) {
        List<User> users = getUsersInInternship(internship.getId());
        List<Task> tasks = new ArrayList<>();

        for (Lesson lesson : internship.getLessons()) {
            tasks.addAll(lesson.getTasks());
        }

        createForks(users, tasks);
    }

    public void createForks(List<User> users, List<Task> tasks) {
        try {
            gitlabServiceGrpcClient.createForks(tasks, users);
        } catch (Exception e) {
            log.error("Error happened during creating forks.", e);
            messageService.noticeAllAdminsOnForkFail(users, tasks, e.getMessage());
            throw new ForkFailedException(e.getMessage());
        }
    }

    private List<User> getUsersInInternship(Integer internshipId) {
        return userInternshipRepository.findAllByInternshipIdAndStatus(internshipId, UserInternshipStatus.JOINED)
                .stream().map(UserOnly::getUser).collect(Collectors.toList());
    }
}
