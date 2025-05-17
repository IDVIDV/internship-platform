package ru.internship.platform.service;

import ru.internship.platform.repository.CommitRepository;
import ru.internship.platform.dto.CommitDto;
import ru.internship.platform.entity.Commit;
import ru.internship.platform.mapper.CommitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommitService {
    private final CommitRepository commitRepository;
    private final CommitMapper commitMapper;

    public CommitDto getUncheckedCommitById(Integer commitId) {
        Commit commit = commitRepository.findByIdAndTaskForkAccepted(commitId, false);

        if (commit == null) {
            throw new EntityNotFoundException(String.format("Commit with id %d does not exist or was accepted",
                    commitId));
        }

        return commitMapper.toCommitDto(commit);
    }

    public List<CommitDto> getAllLatestUncheckedCommits(Integer lessonId) {
        return commitMapper.toCommitDtoList(commitRepository
                .findAllLatestUncheckedCommitsByLessonId(lessonId));
    }
}
