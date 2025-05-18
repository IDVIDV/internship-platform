package ru.internship.platform.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.gitlab4j.api.GitLabApiException;
import ru.internship.platform.proto.GitlabServiceGrpc;
import ru.internship.platform.proto.GitlabServiceOuterClass;
import ru.internship.platform.proto.GitlabServiceOuterClass.CreateTaskForksResponse;
import ru.internship.platform.service.GitlabService;

@GrpcService
@RequiredArgsConstructor
public class GitlabServiceGrpcServer extends GitlabServiceGrpc.GitlabServiceImplBase {

    private final GitlabService gitlabService;

    @Override
    public void createTaskForks(
            GitlabServiceOuterClass.CreateTaskForksRequest request,
            StreamObserver<CreateTaskForksResponse> responseObserver
    ) {
        CreateTaskForksResponse.Builder builder =
                CreateTaskForksResponse.newBuilder()
                .setExceptionMessage("");

        try {
            for (int userId : request.getUserIdsList()) {
                gitlabService.createForks(request.getTaskIdsList(), userId);
            }
        } catch (GitLabApiException e) {
            builder.setExceptionMessage(e.getMessage());
        }

        responseObserver.onNext(builder.build());

        responseObserver.onCompleted();
    }
}
