package ru.internship.platform.controller;

import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.internship.platform.service.GitlabService;

@RestController
@RequiredArgsConstructor
public class GitlabController {
    private final GitlabService gitlabService;

    @Value("${GITLAB_PUSH_HOOK_SECRET_TOKEN:secret-token}")
    private String pushEventSecretToken;

    @PostMapping("/gitlab/push-event")
    public void handlePushEvent(
            @RequestBody
            PushEvent pushEvent,

            @RequestHeader(name = "X-Gitlab-Token")
            String secretToken) {
        if (secretToken == null || !secretToken.equals(pushEventSecretToken)) {
            return;
        }

        gitlabService.handlePushEvent(pushEvent);
    }
}
