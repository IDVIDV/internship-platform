package ru.internship.platform.service;

import ru.internship.platform.exception.UnauthorizedException;
import ru.internship.platform.exception.UserAlreadyRegisteredException;
import ru.internship.platform.repository.InternshipRepository;
import ru.internship.platform.repository.UserRepository;
import ru.internship.platform.dto.MessageDto;
import ru.internship.platform.dto.Performance;
import ru.internship.platform.dto.RegisterUser;
import ru.internship.platform.entity.Internship;
import ru.internship.platform.entity.Role;
import ru.internship.platform.entity.User;
import ru.internship.platform.entity.UserInternship;
import ru.internship.platform.entity.status.InternshipStatus;
import ru.internship.platform.entity.status.UserInternshipStatus;
import ru.internship.platform.exception.UserAlreadyJoinedInternshipException;
import ru.internship.platform.exception.UserAlreadyLeftInternshipException;
import ru.internship.platform.exception.InternshipRegistryClosedException;
import ru.internship.platform.mapper.TaskForkMapper;
import ru.internship.platform.mapper.UserMapper;
import ru.internship.platform.repository.TaskForkRepository;
import ru.internship.platform.repository.UserInternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final InternshipRepository internshipRepository;
    private final UserInternshipRepository userInternshipRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MessageService messageService;
    private final TaskForkRepository taskForkRepository;
    private final TaskForkMapper taskForkMapper;
    private final ArchiveService archiveService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public void registerUser(RegisterUser registerUser) {
        User foundUser = userRepository.findByUsername(registerUser.getUsername());

        if (foundUser != null) {
            throw new UserAlreadyRegisteredException("Username is taken");
        }

        User user = userMapper.toUser(registerUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    public void signInInternship(Integer internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);

        if (internship == null) {
            throw new EntityNotFoundException(String.format("Internship with id %d does not exist", internshipId));
        }

        if (!internship.getStatus().equals(InternshipStatus.REGISTRY)) {
            throw new InternshipRegistryClosedException(String.format("Internship with id %d is not in %s state",
                            internshipId, InternshipStatus.REGISTRY));
        }

        User user = getAuthenticatedUser();

        UserInternship userInternship = userInternshipRepository
                .findByInternshipIdAndUserId(internshipId, user.getId());
        UserInternship userInternshipToSave;

        if (userInternship != null) {
            if (userInternship.getStatus().equals(UserInternshipStatus.JOINED)) {
                throw new UserAlreadyJoinedInternshipException(String.format("User with id %d already joined internship with id %d",
                        user.getId(), internshipId));
            }

            userInternshipToSave = userInternship;
            userInternshipToSave.setStatus(UserInternshipStatus.JOINED);
        } else {
            userInternshipToSave = new UserInternship();
            userInternshipToSave.setInternship(internship);
            userInternshipToSave.setUser(user);
            userInternshipToSave.setStatus(UserInternshipStatus.JOINED);
        }

        userInternshipRepository.save(userInternshipToSave);
    }

    public void leaveInternship(Integer internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);

        if (internship == null) {
            throw new EntityNotFoundException(String.format("Internship with id %d does not exist", internshipId));
        }

        User user = getAuthenticatedUser();

        UserInternship userInternship = userInternshipRepository
                .findByInternshipIdAndUserId(internshipId, user.getId());
        UserInternship userInternshipToSave;

        if (userInternship != null) {
            if (userInternship.getStatus().equals(UserInternshipStatus.LEFT)) {
                throw new UserAlreadyLeftInternshipException(String.format("User with id %d already left internship with id %d",
                        user.getId(), internshipId));
            }

            userInternshipToSave = userInternship;
            userInternshipToSave.setStatus(UserInternshipStatus.LEFT);
        } else {
            userInternshipToSave = new UserInternship();
            userInternshipToSave.setInternship(internship);
            userInternshipToSave.setUser(user);
            userInternshipToSave.setStatus(UserInternshipStatus.LEFT);
        }

        userInternshipRepository.save(userInternshipToSave);

        if (internship.getStatus() != InternshipStatus.REGISTRY) {
            List<User> userToArchive = new ArrayList<>();
            userToArchive.add(user);
            archiveService.archiveUsersInInternship(userToArchive, internship);
        }
    }

    public List<Performance> getPerformance() {
        User user = getAuthenticatedUser();

        return taskForkMapper.toPerformanceList(taskForkRepository.findAllByUserId(user.getId()));
    }

    public List<MessageDto> getMessages() {
        User user = getAuthenticatedUser();

        return messageService.getMessagesByReceiverId(user.getId());
    }

    private User getAuthenticatedUser() {
        if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            throw new UnauthorizedException("User is not authorized");
        } else {
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
    }
}

