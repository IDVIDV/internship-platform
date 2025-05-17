package ru.internship.platform.service;

import ru.internship.platform.exception.WrongInternshipStatusException;
import ru.internship.platform.repository.InternshipRepository;
import ru.internship.platform.dto.AddInternship;
import ru.internship.platform.dto.InternshipDto;
import ru.internship.platform.dto.InternshipItem;
import ru.internship.platform.entity.Internship;
import ru.internship.platform.entity.User;
import ru.internship.platform.entity.status.InternshipStatus;
import ru.internship.platform.entity.status.UserInternshipStatus;
import ru.internship.platform.mapper.InternshipMapper;
import ru.internship.platform.repository.UserInternshipRepository;
import ru.internship.platform.repository.projection.UserOnly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final UserInternshipRepository userInternshipRepository;
    private final InternshipMapper internshipMapper;
    private final TaskForkService taskForkService;
    private final ArchiveService archiveService;

    public List<InternshipItem> getAllInternships() {
        return internshipMapper.toInternshipItemList(internshipRepository.findAll());
    }

    @Cacheable(cacheNames = "internship", key = "#internshipId")
    public InternshipDto getInternshipById(Integer internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);

        if (internship == null) {
            throw new EntityNotFoundException(String.format("Internship with id %d does not exist", internshipId));
        }

        return internshipMapper.toInternshipDto(internship);
    }

    @CachePut(cacheNames = "internship", key = "#result.id" )
    public InternshipDto addInternship(AddInternship addInternship) {
        Internship internship = internshipMapper.toInternship(addInternship);
        internship.setStatus(InternshipStatus.REGISTRY);
        internshipRepository.save(internship);
        return internshipMapper.toInternshipDto(internship);
    }

    @CacheEvict(cacheNames = "internship", key = "#internshipId")
    public void endRegistry(Integer internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);

        if (internship == null) {
            throw new EntityNotFoundException(String.format("Internship with id %d does not exist", internshipId));
        }

        if (!internship.getStatus().equals(InternshipStatus.REGISTRY)) {
            throw new WrongInternshipStatusException(String.format("Internship should be in %s state",
                    InternshipStatus.REGISTRY));
        }

        internship.setStatus(InternshipStatus.AWAITING_START);
        internshipRepository.save(internship);

        log.info("Internship transferred from {} to {} state",
                InternshipStatus.REGISTRY, InternshipStatus.AWAITING_START);
    }

    @CacheEvict(cacheNames = "internship", key = "#internshipId")
    public void startInternship(Integer internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);

        if (internship == null) {
            throw new EntityNotFoundException(String.format("Internship with id %d does not exist", internshipId));
        }

        if (!internship.getStatus().equals(InternshipStatus.AWAITING_START)) {
            throw new WrongInternshipStatusException(String.format("Internship should be in %s state",
                    InternshipStatus.AWAITING_START));
        }

        taskForkService.createForksOnInternshipStart(internship);

        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internshipRepository.save(internship);

        log.info("Internship transferred from {} to {} state",
                InternshipStatus.AWAITING_START, InternshipStatus.IN_PROGRESS);
    }

    @CacheEvict(cacheNames = "internship", key = "#internshipId")
    public void endInternship(Integer internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);

        if (internship == null) {
            throw new EntityNotFoundException(String.format("Internship with id %d does not exist", internshipId));
        }

        if (!internship.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
            throw new WrongInternshipStatusException(String.format("Internship should be in %s state",
                    InternshipStatus.IN_PROGRESS));
        }

        List<User> usersInInternship = userInternshipRepository
                .findAllByInternshipIdAndStatus(internship.getId(), UserInternshipStatus.JOINED)
                .stream()
                .map(UserOnly::getUser)
                .collect(Collectors.toList());

        archiveService.archiveUsersInInternship(usersInInternship, internship);

        internship.setStatus(InternshipStatus.CLOSED);
        internshipRepository.save(internship);

        log.info("Internship transferred from {} to {} state",
                InternshipStatus.IN_PROGRESS, InternshipStatus.CLOSED);
    }
}
