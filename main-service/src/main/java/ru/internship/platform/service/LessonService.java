package ru.internship.platform.service;

import ru.internship.platform.repository.InternshipRepository;
import ru.internship.platform.dto.AddLesson;
import ru.internship.platform.dto.LessonDto;
import ru.internship.platform.dto.LessonItem;
import ru.internship.platform.entity.Internship;
import ru.internship.platform.entity.Lesson;
import ru.internship.platform.mapper.LessonMapper;
import ru.internship.platform.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final InternshipRepository internshipRepository;
    private final LessonMapper lessonMapper;
    private final LessonRepository lessonRepository;

    public List<LessonItem> getAllLessonsByInternshipId(Integer internshipId) {
        return lessonMapper.toLessonItemList(lessonRepository.findAllByInternshipId(internshipId));
    }

    public LessonDto getLessonById(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);

        if (lesson == null) {
            throw new EntityNotFoundException(String.format("Lesson with id %d does not exist", lessonId));
        }

        return lessonMapper.toLessonDto(lesson);
    }

    public void addLesson(AddLesson addLesson) {
        Internship internship = internshipRepository.findById(addLesson.getInternshipId()).orElse(null);

        if (internship == null) {
            throw new EntityNotFoundException(String.format("Internship with id %d does not exist",
                    addLesson.getInternshipId()));
        }

        Lesson lesson = lessonMapper.toLesson(addLesson);
        lessonRepository.save(lesson);
    }
}
