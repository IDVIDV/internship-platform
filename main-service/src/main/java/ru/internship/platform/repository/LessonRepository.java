package ru.internship.platform.repository;

import ru.internship.platform.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findAllByInternshipId(Integer internshipId);
}
