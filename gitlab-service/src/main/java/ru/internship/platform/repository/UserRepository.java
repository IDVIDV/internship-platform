package ru.internship.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.internship.platform.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> { }
