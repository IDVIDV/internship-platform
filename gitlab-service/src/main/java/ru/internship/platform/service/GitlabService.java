package ru.internship.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Author;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.webhook.EventCommit;
import org.gitlab4j.api.webhook.EventRepository;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.internship.platform.entity.Commit;
import ru.internship.platform.entity.Task;
import ru.internship.platform.entity.TaskFork;
import ru.internship.platform.entity.User;
import ru.internship.platform.repository.CommitRepository;
import ru.internship.platform.repository.TaskForkRepository;
import ru.internship.platform.repository.TaskRepository;
import ru.internship.platform.repository.UserRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitlabService {
    private final UserRepository userRepository;
    private final CommitRepository commitRepository;
    private final TaskRepository taskRepository;
    private final TaskForkRepository taskForkRepository;
    private final GitLabApi gitLabApi;

    @Value("${GITLAB_USER_PASSWORD:d7NTEU4RjG3bwWua}")
    private String gitlabDefaultUserPassword;

    public void handlePushEvent(PushEvent pushEvent) {
        EventRepository eventRepository = pushEvent.getRepository();

        if (eventRepository == null) {
            log.info("Push event {} rejected because of null eventRepository", pushEvent);
            return;
        }

        TaskFork taskFork = taskForkRepository.findByUrl(eventRepository.getHomepage());

        if (taskFork == null) {
            log.info("Push event {} rejected because of unregistered fork", pushEvent);
            return;
        }

        List<EventCommit> eventCommits = pushEvent.getCommits();
        List<Commit> commitsToSave = new ArrayList<>();

        for (EventCommit eventCommit : eventCommits) {
            Commit commit = new Commit();
            Author author = eventCommit.getAuthor();
            commit.setAuthor(author == null ? null : author.getName());
            commit.setCommitDate(Timestamp.from(eventCommit.getTimestamp().toInstant()));
            commit.setUrl(eventCommit.getUrl());
            commit.setTaskFork(taskFork);
            commitsToSave.add(commit);
        }

        commitRepository.saveAll(commitsToSave);

        log.info("Push event handled: {}; saved commits: {}", pushEvent, commitsToSave);
    }

    public void createForks(List<Integer> taskIds, Integer userId) throws GitLabApiException {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new GitLabApiException("User not found by id " + userId);
        }

        List<Task> tasks = taskRepository.findAllById(taskIds);

        if (tasks.isEmpty()) {
            throw new GitLabApiException("No tasks given");
        }

        org.gitlab4j.api.models.User gitlabUser = gitLabApi.getUserApi().getUser(user.getUsername());

        if (gitlabUser == null) {
            gitlabUser = registerUser(user);
        }

        List<TaskFork> taskForks = new ArrayList<>();

        for (Task task : tasks) {
            Project fork = gitLabApi.getProjectApi()
                    .forkProject(task.getPath(), gitlabUser.getNamespaceId());
            TaskFork taskFork = new TaskFork(null, task, user,
                    false, fork.getWebUrl(), null);
            taskForks.add(taskFork);
        }

        log.info("Forks created for user {} of tasks {}", user, tasks);
    }

    public org.gitlab4j.api.models.User registerUser(User user) throws GitLabApiException {
        org.gitlab4j.api.models.User gitlabUser = gitLabApi.getUserApi().getUser(user.getUsername());

        if (gitlabUser != null) {
            return gitlabUser;
        }

        gitlabUser = new org.gitlab4j.api.models.User()
                .withSkipConfirmation(true)
                .withEmail(user.getMail())
                .withUsername(user.getUsername())
                .withName(user.getFullName());

        gitlabUser = gitLabApi.getUserApi().createUser(gitlabUser,
                gitlabDefaultUserPassword, false);

        log.info("New user registered in gitlab: {}; created account info: {}", user, gitlabUser);

        return gitlabUser;
    }
}
