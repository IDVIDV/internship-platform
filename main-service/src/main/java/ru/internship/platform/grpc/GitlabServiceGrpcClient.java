package ru.internship.platform.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.internship.platform.entity.Task;
import ru.internship.platform.entity.User;
import ru.internship.platform.exception.GitlabServiceException;
import ru.internship.platform.proto.GitlabServiceGrpc.GitlabServiceBlockingStub;
import ru.internship.platform.proto.GitlabServiceOuterClass.CreateTaskForksRequest;
import ru.internship.platform.proto.GitlabServiceOuterClass.CreateTaskForksResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GitlabServiceGrpcClient {
    @GrpcClient("gitlab")
    private GitlabServiceBlockingStub stub;

    public void createForks(List<Task> tasks, List<User> users) throws GitlabServiceException {
        CreateTaskForksRequest request = CreateTaskForksRequest
                .newBuilder()
                .addAllUserIds(users.stream().map(User::getId).collect(Collectors.toList()))
                .addAllTaskIds(tasks.stream().map(Task::getId).collect(Collectors.toList()))
                .build();

        CreateTaskForksResponse response = stub.createTaskForks(request);

        if (!response.getExceptionMessage().isEmpty()) {
            throw new GitlabServiceException(response.getExceptionMessage());
        }
    }
}
