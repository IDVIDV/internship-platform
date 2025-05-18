package ru.internship.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.internship.platform.entity.TaskFork;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskForkRepository extends JpaRepository<TaskFork, Integer> {
    TaskFork findByUrl(String Url);
}
