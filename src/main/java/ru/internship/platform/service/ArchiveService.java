package ru.internship.platform.service;

import ru.internship.platform.entity.Internship;
import ru.internship.platform.entity.User;
import ru.internship.platform.entity.archive.ArchiveInternship;
import ru.internship.platform.mapper.InternshipMapper;
import ru.internship.platform.mapper.TaskForkMapper;
import ru.internship.platform.mapper.UserInternshipMapper;
import ru.internship.platform.mapper.UserMapper;
import ru.internship.platform.repository.TaskForkRepository;
import ru.internship.platform.repository.UserInternshipRepository;
import ru.internship.platform.repository.archive.ArchiveInternshipRepository;
import ru.internship.platform.repository.archive.ArchivePerformanceRepository;
import ru.internship.platform.repository.archive.ArchiveUserInternshipRepository;
import ru.internship.platform.repository.archive.ArchiveUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveService {
    private final ArchiveUserRepository archiveUserRepository;
    private final ArchivePerformanceRepository archivePerformanceRepository;
    private final ArchiveUserInternshipRepository archiveUserInternshipRepository;
    private final ArchiveInternshipRepository archiveInternshipRepository;
    private final UserInternshipRepository userInternshipRepository;
    private final TaskForkRepository taskForkRepository;
    private final UserMapper userMapper;
    private final UserInternshipMapper userInternshipMapper;
    private final TaskForkMapper taskForkMapper;
    private final InternshipMapper internshipMapper;

    public void archiveUsersInInternship(List<User> users, Internship internship) {
        ArchiveInternship archiveInternship = internshipMapper.toArchiveInternship(internship);

        archiveInternshipRepository.save(archiveInternship);
        archiveUserRepository.saveAll(userMapper.toArchiveUserList(users));

        List<Integer> userIds = users.stream().map(User::getId).collect(Collectors.toList());

        archiveUserInternshipRepository.saveAll(
                userInternshipRepository.findAllByInternshipIdAndUserIdIn(
                                internship.getId(),
                                userIds
                        )
                        .stream()
                        .map(userInternshipMapper::toArchiveUserInternship)
                        .collect(Collectors.toList())
        );

        archivePerformanceRepository.saveAll(
                taskForkRepository.findAllByUserIdInAndTaskLessonInternshipId(userIds, internship.getId())
                        .stream()
                        .map(taskForkMapper::toArchivePerformance)
                        .map(performance -> {
                            performance.setInternship(archiveInternship);
                            return performance;
                        })
                        .collect(Collectors.toList())
        );

        log.info("Archived users {} in internship {}", users, internship);
    }
}
