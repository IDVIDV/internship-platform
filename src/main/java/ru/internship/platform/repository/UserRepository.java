package ru.internship.platform.repository;

import ru.internship.platform.entity.User;
import ru.internship.platform.entity.status.UserInternshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    List<User> findAllByRoleId(Integer roleId);

    List<User> findAllByUserInternshipsInternshipIdAndUserInternshipsStatus(
            Integer internshipId,
            UserInternshipStatus status);
}
