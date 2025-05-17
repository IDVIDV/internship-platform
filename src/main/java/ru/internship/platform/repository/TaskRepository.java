package ru.internship.platform.repository;

import ru.internship.platform.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByLessonId(Integer lessonId);

    List<Task> findAllByLessonInternshipId(Integer internshipId);
}
